package go.alarm.fcm.service;

import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import go.alarm.fcm.config.ApnsConfigBuilder;
import go.alarm.group.domain.Group;
import go.alarm.group.domain.UserGroup;
import go.alarm.group.domain.repository.GroupRepository;
import go.alarm.group.domain.repository.UserGroupRepository;
import go.alarm.user.domain.User;
import go.alarm.user.domain.repository.UserRepository;
import go.alarm.wakeupdayofweek.domain.WakeUpDayOfWeek;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FCMServiceImpl implements FCMService{

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final FirebaseMessaging firebaseMessaging;

    /**
     * 기상 알람을 보내는 메소드입니다.
     * 스케줄러로 매분마다 이 메소드를 실행시킵니다.
     */
    @Override
    public void sendAlarms() {
        LocalTime now = LocalTime.now().withNano(0);
        DayOfWeek dayOfToday = LocalDate.now().getDayOfWeek();
        List<Group> groups = groupRepository.findAllByWakeupTime(now);

        if(!groups.isEmpty()){ // 기상 시간이 현재 시간과 일치한 그룹이 존재한다면
            for (Group group : groups) {
                if (isToday(group, dayOfToday)) {
                    List<UserGroup> userGroups = userGroupRepository.findAllByGroup(group);
                    for (UserGroup userGroup : userGroups) {
                        if (!userGroup.getIsDisturbBanMode()) {
                            User user = userGroup.getUser();
                            try {
                                sendNotification(
                                    user.getFcmToken(),
                                    "기상 알람",
                                    "기상 알랍입니다. 현재 시각: " + now,
                                    "DefaultSound.wav"
                                );
                            } catch (FirebaseMessagingException e) {
                                log.warn("유저" + user.getNickname() + "에게 기상 알람 전송을 실패했습니다.");
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 취침 알람을 보내는 메소드입니다.
     * 스케줄러로 매분마다 이 메소드를 실행시킵니다.
     */
    @Override
    public void sendBedTimeAlarms() {
        LocalTime now = LocalTime.now().withNano(0);
        DayOfWeek dayOfToday = LocalDate.now().getDayOfWeek();
        List<User> users = userRepository.findAllByBedTime(now);

        if(!users.isEmpty()){
            for (User user : users) {
                if (isToday(user, dayOfToday) && user.getIsAllowBedTimeAlarm()) {
                    try {
                        sendNotification(
                            user.getFcmToken(),
                            "취침 알람",
                            "취침 알람입니다. 현재 시각: " + now,
                            "FirstSound.wav"
                        );
                    } catch (FirebaseMessagingException e) {
                        log.warn("유저" + user.getNickname() + "에게 취침 알람 전송을 실패했습니다.");
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * 알람을 테스트함과 동시에 어떤 유저에게 알람을 보냈는지 유저 리스트를 반환합니다.
     */
    @Override
    public User sendAlarmsTest(Long userId) {
        User user = userRepository.findById(userId).get();

        try {
            sendNotification(
                user.getFcmToken(),
                "Test Alarm",
                "It's just Test. Don't Worry",
                "SecondSound.wav"
            );
        } catch (FirebaseMessagingException e) {
            // 로그 기록 또는 에러 처리
            System.err.println("Failed to send notification to user: " + user.getNickname());
            e.printStackTrace();
        }

        return user;
    }

    /**
     * 콕 찌르기를 합니다.
     */
    @Override
    public void prick(Long senderId, Long receiverId) {
        User sender = userRepository.findById(senderId).get();
        User receiver = userRepository.findById(receiverId).get();

        LocalTime now = LocalTime.now().withNano(0);

        try {
            sendNotification(
                receiver.getFcmToken(),
                "콕 찌르기",
                sender.getNickname() + "이/가" + receiver.getNickname() + "을/를" + "콕 찔렀어요!! 현재 시각: " + now,
                "ThirdSound.wav"
            );
        } catch (FirebaseMessagingException e) {
            log.warn("유저" + receiver.getNickname() + "에게 콕 찌르기를 실패했습니다.");
            e.printStackTrace();
        }
    }


    /**
     * FCM으로 알림을 보내줍니다.
     */
    private void sendNotification(String token, String title, String body, String sound) throws FirebaseMessagingException {

        ApnsConfig apnsConfig = ApnsConfigBuilder.buildApnsConfig(sound);

        Message message = Message.builder()
            .setToken(token)
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .setApnsConfig(apnsConfig)
            .build();

        firebaseMessaging.send(message);
    }

    public boolean isToday(Object entity, DayOfWeek dayOfToday) {
        WakeUpDayOfWeek dayOfWeek = null;

        if (entity instanceof Group) {
            dayOfWeek = ((Group) entity).getWakeUpDayOfWeek();
        } else if (entity instanceof User) {
            dayOfWeek = ((User) entity).getBedDayOfWeek();
        }

        if (dayOfWeek == null) {
            return false; // WakeupDate 또는 BedDate가 설정되지 않은 경우
        }

        return switch (dayOfToday) {
            case MONDAY -> dayOfWeek.getMon();
            case TUESDAY -> dayOfWeek.getTue();
            case WEDNESDAY -> dayOfWeek.getWed();
            case THURSDAY -> dayOfWeek.getThu();
            case FRIDAY -> dayOfWeek.getFri();
            case SATURDAY -> dayOfWeek.getSat();
            case SUNDAY -> dayOfWeek.getSun();
            default -> false;
        };
    }
}