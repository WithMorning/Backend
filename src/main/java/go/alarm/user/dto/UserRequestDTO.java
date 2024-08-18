package go.alarm.user.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;

public class UserRequestDTO {

    @Getter
    public static class SetBedTimeDTO{

        @NotNull
        LocalTime bedTime;
        @NotNull
        List<String> bedDayOfWeekList;
        @NotNull
        Boolean isAllowBedTimeAlarm;
    }

}
