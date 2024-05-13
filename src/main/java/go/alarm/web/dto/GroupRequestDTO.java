package go.alarm.web.dto;

import go.alarm.domain.entity.WakeupDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;

public class GroupRequestDTO {

    @Getter
    public static class CreateDTO{
        @NotBlank
        String name;
        @NotNull
        LocalTime wakeupTime;
        @NotNull
        List<String> wakeupDateList;
        @NotNull
        Boolean isAgree;

        String memo;
    }


    @Getter
    public static class JoinGroupDTO{
        @NotNull
        String participationCode;
        @NotNull
        Boolean isAgree;
    }

    @Getter
    public static class InviteGroupDTO{
        @NotNull
        String phone;
        @NotNull
        Boolean isAgree;
    }
}
