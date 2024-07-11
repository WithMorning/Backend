package go.alarm.config;

import go.alarm.service.AlarmService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    private final AlarmService alarmService;

    public SchedulerConfig(AlarmService alarmService) {
        this.alarmService = alarmService;
    }

    /*
    * "0 0 * * * *": 매 시간 정각에 실행
    * "0 0 9 * * *": 매일 오전 9시에 실행
    * "0 0 9 * * MON-FRI": 평일 오전 9시에 실행
    * */

    @Scheduled(cron = "0 * * * * *") // 매분 실행
    public void scheduleAlarms() {
        //alarmService.sendAlarms();
        alarmService.sendAlarmsTest(); // groupId = 10인 그룹 유저들에게 알람을 보내는 테스트
    }
}