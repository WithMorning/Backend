package go.alarm.global.config;

import go.alarm.fcm.service.FCMService;
import go.alarm.user.service.UserService;
import java.time.LocalTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
public class SchedulerConfig {
    private final FCMService fcmService;
    private final UserService userService;

    public SchedulerConfig(FCMService fcmService, UserService userService) {
        this.fcmService = fcmService;
        this.userService = userService;
    }

    /*
    * "0 * * * * *": 매 분에 실행
    * "0 0 * * * *": 매 시간 정각에 실행
    * "0 0 9 * * *": 매일 오전 9시에 실행
    * "0 0 9 * * MON-FRI": 평일 오전 9시에 실행
    * */

    @Scheduled(cron = "0 * * * * *")
    public void scheduleAlarms() {
        log.warn("현재 시각:" + LocalTime.now());
        //fcmService.sendAlarms();
        fcmService.sendBedTimeAlarms();
    }

    @Scheduled(cron = "0 59 23 * * *")
    public void setSleepStatus() {
        userService.setSleepStatus();
    }

}