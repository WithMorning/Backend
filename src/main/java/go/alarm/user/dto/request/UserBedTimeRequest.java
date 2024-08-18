package go.alarm.user.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
public class UserBedTimeRequest {

    @NotNull(message = "유저의 취침 시간을 입력해주세요.")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime bedTime;

    @NotNull(message = "유저의 취침 시간 알림을 받을 요일을 입력해주세요.")
    private List<String> bedDayOfWeekList;

    @NotNull(message = "유저의 취침 알림 수신 동의 여부를 입력해주세요.")
    private Boolean isAllowBedTimeAlarm;

}
