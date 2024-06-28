package go.alarm.web.dto;

import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MainResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MainDTO{
        String connectorProfileURL; // 접속자의 프로필 이미지 URL
        List<AlarmDTO> alarmList;
        Integer listSize;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlarmDTO{
        String name;
        LocalTime wakeupTime;
        List<String> wakeupDateList;
        Boolean isDisturbBanMode; // 메인페이지의 알람 허용 바
        List<UserDTO> userList;
        String memo;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDTO{
        String imageURL;
        String nickname;
        Boolean isWakeup;
        Boolean isDisturbBanMode;
    }
}
