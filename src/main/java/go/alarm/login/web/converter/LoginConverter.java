package go.alarm.login.web.converter;

import go.alarm.domain.entity.User;
import go.alarm.login.domain.RefreshToken;
import go.alarm.login.dto.AccessToken;


public class LoginConverter {

    public static User toUser(final String socialLoginId, final String email){
        return User.builder()
            .socialLoginId(socialLoginId)
            .email(email)
            .build();
    }

    public static RefreshToken toRefreshToken(final String refreshToken, final Long userId){

        return RefreshToken.builder()
            .refreshToken(refreshToken)
            .userId(userId)
            .build();
    }

    public static AccessToken toAccessToken(final String accessToken){

        return AccessToken.builder()
            .accessToken(accessToken)
            .build();
    }
}
