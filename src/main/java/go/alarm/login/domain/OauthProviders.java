package go.alarm.login.domain;

import static go.alarm.global.response.ResponseCode.NOT_SUPPORTED_OAUTH_SERVICE;

import go.alarm.global.response.exception.AuthException;
import java.util.List;
import org.springframework.stereotype.Component;

/*
* OauthProvider를 저장하는 객체
* */
@Component
public class OauthProviders {

    private final List<OauthProvider> providers;

    public OauthProviders(final List<OauthProvider> providers) {
        this.providers = providers;
    }

    public OauthProvider mapping(final String providerName) {
        return providers.stream()
            .filter(provider -> provider.is(providerName))
            .findFirst()
            .orElseThrow(() -> new AuthException(NOT_SUPPORTED_OAUTH_SERVICE));
    }
}