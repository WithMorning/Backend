package go.alarm.login.presentation;

import go.alarm.user.domain.User;
import go.alarm.login.domain.RefreshToken;
import go.alarm.user.domain.UserState;

public class LoginConverter {

    public static User toUser(final String socialLoginId, final String email){
        return User.builder()
            .socialLoginId(socialLoginId)
            .email(email)
            .status(UserState.ACTIVE)
            .isCertify(true)
            .build();
    }

    public static RefreshToken toRefreshToken(final String refreshToken, final Long userId){

        return RefreshToken.builder()
            .refreshToken(refreshToken)
            .userId(userId)
            .build();
    }
}
