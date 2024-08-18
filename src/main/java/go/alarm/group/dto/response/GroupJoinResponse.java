package go.alarm.group.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupJoinResponse {

    private Long groupId;
    private String joinUserNickname;
    private LocalDateTime joinAt;

}
