package go.alarm.service;

import go.alarm.domain.entity.Group;
import go.alarm.domain.entity.User;
import go.alarm.domain.entity.UserGroup;
import go.alarm.domain.entity.WakeupDate;
import go.alarm.domain.repository.GroupRepository;
import go.alarm.domain.repository.UserGroupRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AlarmServiceImpl implements AlarmService{

    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
    //private final FCMService fcmService; 일단 1단계는 FCM없이 알림을 콘솔로 찍어보자.

    /*
    * 알람을 보내는 메소드
    * 스케줄러로 매분마다 이 메소드를 실행시킨다.
    * */
    @Override
    public void sendAlarms() {
        LocalTime now = LocalTime.now().withNano(0);
        DayOfWeek dayOfToday = LocalDate.now().getDayOfWeek();

        List<Group> groups = groupRepository.findAllByWakeupTime(now);

        if(!groups.isEmpty()){ // 기상 시간이 현재 시간과 일치한 그룹이 존재한다면
            for (Group group : groups) {
                if (isWakeupDay(group, dayOfToday)) {
                    System.out.println("Yes Today");
                    List<UserGroup> userGroups = userGroupRepository.findAllByGroup(group);
                    for (UserGroup userGroup : userGroups) {
                        if (!userGroup.getIsDisturbBanMode()) {
                            System.out.println("Not BanMode");
                            User user = userGroup.getUser();
                            System.out.println("알람 전송: 사용자 " + user.getNickname() + "님, 일어나세요! 현재 시간: " + now);
                        }
                    }
                }
            }
        }
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
