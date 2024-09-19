package go.alarm.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserPrickRequest {

    @NotNull(message = "유저의 id를 입력해주세요.")
    private Long userId;
}
