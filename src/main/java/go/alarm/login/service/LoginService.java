package go.alarm.login.service;

import go.alarm.login.domain.UserTokens;
import java.io.IOException;

public interface LoginService {
    UserTokens login(final String providerName, final String identityToken, final String code)
        throws IOException;

    String renewalAccessToken(final String refreshTokenRequest, final String authorizationHeader);

    void removeRefreshToken(final String refreshToken);

    void deleteAccount(final Long memberId, String providerName);
}
