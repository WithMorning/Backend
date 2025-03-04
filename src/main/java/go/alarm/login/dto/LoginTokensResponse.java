package go.alarm.login.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class LoginTokensResponse {

    private String accessToken;

    @JsonInclude(JsonInclude.Include.NON_NULL) // 어노테이션이 적용된 필드가 null인 경우, JSON 응답에서 해당 필드는 제외
    private String refreshToken;

    private Boolean isNewUser;

    public LoginTokensResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public LoginTokensResponse(String accessToken, String refreshToken, Boolean isNewUser) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.isNewUser = isNewUser;
    }
}
