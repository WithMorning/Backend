package go.alarm.home.dto.response;

import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class HomeResponse {

    @Builder
    @Getter
    public static class HomeDTO{
        private String connectorProfileURL; // 접속자의 프로필 이미지 URL
        private List<GroupDTO> groupList;
        private Integer listSize;
    }

    @Builder
    @Getter
    public static class GroupDTO {
        private Long groupId;
        private String name;
        private LocalTime wakeupTime;
        private List<String> dayOfWeekList;
        private List<UserDTO> userList;
        private String memo;
    }

    @Builder
    @Getter
    public static class UserDTO {
        private Long userId;
        private String imageURL;
        private String nickname;
        private Boolean isWakeup;
        private Boolean isDisturbBanMode;
        private String phone;
    }
}
