package go.alarm.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import go.alarm.domain.entity.Group;
import go.alarm.domain.entity.User;
import go.alarm.domain.entity.UserGroup;
import go.alarm.domain.entity.WakeupDate;
import go.alarm.domain.repository.GroupRepository;
import go.alarm.domain.repository.UserGroupRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AlarmServiceImpl implements AlarmService{

    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
    private final FCMService fcmService;

    /**
     * 알람을 보내는 메소드
     * 스케줄러로 매분마다 이 메소드를 실행시킨다.
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
                                fcmService.sendNotification(
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
     * 알람을 테스트함과 동시에 어떤 유저에게 알람을 보냈는지 리스트를 반환합니다.
     */
    @Override
    public List<User> sendAlarmsTest() {
        // DB에 groupId가 존재하는지 꼭 확인해줘야 함.(여기선 groupId = 10)
        Group group = groupRepository.findById(Long.valueOf(10)).get();
        ArrayList<User> userList = new ArrayList<>();

        List<UserGroup> userGroups = userGroupRepository.findAllByGroup(group);
        for (UserGroup userGroup : userGroups) {
            if (!userGroup.getIsDisturbBanMode()) {
                User user = userGroup.getUser();
                userList.add(user); // 어떤 유저에게 알림을 보냈는지 리스트에 담는다.

                try {
                    fcmService.sendNotification(
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

    private boolean isWakeupDay(Group group, DayOfWeek dayOfToday) {
        WakeupDate wakeupDate = group.getWakeupDate();

        if (wakeupDate == null) {
            return false; // WakeupDate가 설정되지 않은 경우
        }

        switch (dayOfToday) {
            case MONDAY:
                return wakeupDate.getMon();
            case TUESDAY:
                return wakeupDate.getTue();
            case WEDNESDAY:
                return wakeupDate.getWed();
            case THURSDAY:
                return wakeupDate.getThu();
            case FRIDAY:
                return wakeupDate.getFri();
            case SATURDAY:
                return wakeupDate.getSat();
            case SUNDAY:
                return wakeupDate.getSun();
            default:
                return false; // 예상치 못한 요일 값
        }
    }
}
