package go.alarm.group.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupResponse {

    private Long groupId;
    private LocalDateTime createdAt;
}
