package go.alarm.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@Getter
public class LoginTokenResponse {

    private String accessToken;
    private String refreshToken;

    public LoginTokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public LoginTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
