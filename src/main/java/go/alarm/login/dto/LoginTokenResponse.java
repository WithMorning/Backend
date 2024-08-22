package go.alarm.login.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@Getter
public class LoginTokenResponse {

    private String accessToken;

    @JsonInclude(JsonInclude.Include.NON_NULL) // 어노테이션이 적용된 필드가 null인 경우, JSON 응답에서 해당 필드는 제외
    private String refreshToken;

    public LoginTokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public LoginTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
