package go.alarm.login.domain;

import java.io.IOException;
import org.springframework.web.client.RestTemplate;

public interface OauthProvider {

    RestTemplate restTemplate = new RestTemplate();

    boolean is(String name);
    OauthUserInfo getUserInfo(String code);

    Boolean revokeToken(Long userId, String appleRefreshToken);

    String getRefreshToken(String authorizationCode) throws IOException;
}