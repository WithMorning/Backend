package go.alarm.home.dto;

import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class HomeResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomeDTO{
        String connectorProfileURL; // 접속자의 프로필 이미지 URL
        List<GroupDTO> groupList;
        Integer listSize;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupDTO {
        Long groupId;
        String name;
        LocalTime wakeupTime;
        List<String> dayOfWeekList;
        List<UserDTO> userList;
        String memo;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDTO {
        Long userId;
        String imageURL;
        String nickname;
        Boolean isWakeup;
        Boolean isDisturbBanMode;
        String phone;
    }
}
