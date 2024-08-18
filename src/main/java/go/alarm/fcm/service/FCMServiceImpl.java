package go.alarm.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import go.alarm.group.domain.Group;
import go.alarm.group.domain.UserGroup;
import go.alarm.group.domain.repository.GroupRepository;
import go.alarm.group.domain.repository.UserGroupRepository;
import go.alarm.user.domain.User;
import go.alarm.wakeupdayofweek.domain.WakeUpDayOfWeek;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FCMServiceImpl implements FCMService{

    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
    private final FirebaseMessaging firebaseMessaging;

    /**
     * 알람을 보내는 메소드입니다.
     * 스케줄러로 매분마다 이 메소드를 실행시킵니다.
     */
    @Override
    public void sendAlarms() {
        LocalTime now = LocalTime.now().withNano(0);
        DayOfWeek dayOfToday = LocalDate.now().getDayOfWeek();
        List<Group> groups = groupRepository.findAllByWakeupTime(now);

        if(!groups.isEmpty()){ // 기상 시간이 현재 시간과 일치한 그룹이 존재한다면
            for (Group group : groups) {
                if (isWakeupDay(group, dayOfToday)) {
                    List<UserGroup> userGroups = userGroupRepository.findAllByGroup(group);
                    for (UserGroup userGroup : userGroups) {
                        if (!userGroup.getIsDisturbBanMode()) {
                            User user = userGroup.getUser();
                            try {
                                sendNotification(
                                    user.getFcmToken(),
                                    "Wake Up Alarm",
                                    "It's time to wake up! Current time: " + now
                                );
                            } catch (FirebaseMessagingException e) {
                                // 로그 기록 또는 에러 처리
                                System.err.println("Failed to send notification to user: " + user.getNickname());
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 알람을 테스트함과 동시에 어떤 유저에게 알람을 보냈는지 유저 리스트를 반환합니다.
     */
    @Override
    public List<User> sendAlarmsTest() {
        // DB에 groupId가 존재하는지 꼭 확인해줘야 함.(여기선 groupId = 1)
        Group group = groupRepository.findById(Long.valueOf(1)).get();
        ArrayList<User> userList = new ArrayList<>();

        List<UserGroup> userGroups = userGroupRepository.findAllByGroup(group);
        for (UserGroup userGroup : userGroups) {
            if (!userGroup.getIsDisturbBanMode()) {
                User user = userGroup.getUser();
                userList.add(user); // 어떤 유저에게 알림을 보냈는지 리스트에 담는다.

                try {
                    sendNotification(
                        user.getFcmToken(),
                        "Test Alarm",
                        "It's just Test. Don't Worry"
                    );
                } catch (FirebaseMessagingException e) {
                    // 로그 기록 또는 에러 처리
                    System.err.println("Failed to send notification to user: " + user.getNickname());
                    e.printStackTrace();
                }
            }
        }
        return userList;
    }

    /**
     * FCM으로 알림을 보내줍니다.
     */
    public void sendNotification(String token, String title, String body) throws FirebaseMessagingException {
        // 이 메시지 부분을 DTO로 따로 빼서 형식을 정할 수도 있을듯. 나중에 리펙토링하면서 고치자.
        Message message = Message.builder()
            .setToken(token)
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .build();

        firebaseMessaging.send(message);
    }

    private boolean isWakeupDay(Group group, DayOfWeek dayOfToday) {
        WakeUpDayOfWeek dayOfWeek = group.getWakeUpDayOfWeek();

        if (dayOfWeek == null) {
            return false; // WakeupDate가 설정되지 않은 경우
        }

        switch (dayOfToday) {
            case MONDAY:
                return dayOfWeek.getMon();
            case TUESDAY:
                return dayOfWeek.getTue();
            case WEDNESDAY:
                return dayOfWeek.getWed();
            case THURSDAY:
                return dayOfWeek.getThu();
            case FRIDAY:
                return dayOfWeek.getFri();
            case SATURDAY:
                return dayOfWeek.getSat();
            case SUNDAY:
                return dayOfWeek.getSun();
            default:
                return false; // 예상치 못한 요일 값
        }
    }
}