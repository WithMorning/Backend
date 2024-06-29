package go.alarm.web.dto;

import go.alarm.domain.entity.WakeupDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;

public class GroupRequestDTO {

    @Getter
    public static class CreateGroupDTO{ // 여기 DTO UI대로 데이터 순서 바꿔야 함. UI에는 시간, 반복 요일이 맨 위에 있음
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
    public static class UpdateGroupDTO{
        LocalTime wakeupTime;
        List<String> wakeupDateList;
        String name;
        String memo;
    }


    @Getter
    public static class JoinGroupDTO{
        @NotNull
        String participationCode;
        @NotNull
        Boolean isAgree;
    }
}
