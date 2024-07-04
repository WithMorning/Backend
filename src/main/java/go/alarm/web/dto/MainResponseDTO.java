package go.alarm.web.dto;

import go.alarm.domain.entity.Group;
import go.alarm.domain.entity.User;
import java.time.LocalTime;
import java.util.HashMap;
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
        List<String> wakeupDateList;
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
