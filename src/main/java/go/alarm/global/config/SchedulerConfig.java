package go.alarm.global.config;

import go.alarm.fcm.service.FCMService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    private final FCMService fcmService;

    public SchedulerConfig(FCMService fcmService) {
        this.fcmService = fcmService;
    }

    /*
    * "0 * * * * *": 매 분에 실행
    * "0 0 * * * *": 매 시간 정각에 실행
    * "0 0 9 * * *": 매일 오전 9시에 실행
    * "0 0 9 * * MON-FRI": 평일 오전 9시에 실행
    * */

    @Scheduled(cron = "0 * * * * *")
    public void scheduleAlarms() {
        fcmService.sendAlarms();
        fcmService.sendBedTimeAlarms();
    }

}