package go.alarm.login.infrastructure;


import static go.alarm.global.response.ResponseCode.INVALID_ACCESS_TOKEN;

import go.alarm.global.response.exception.InvalidJwtException;
import org.springframework.stereotype.Component;

/**
 *  "Bearer " 접두사를 제거하고 토큰만 반환
 * */
@Component
public class BearerAuthorizationExtractor {

    private static final String BEARER_TYPE = "Bearer ";

    public String extractAccessToken(String header) {
        if (header != null && header.startsWith(BEARER_TYPE)) {
            return header.substring(BEARER_TYPE.length()).trim();
        }
        throw new InvalidJwtException(INVALID_ACCESS_TOKEN);
    }
}