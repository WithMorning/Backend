package go.alarm.login.infrastructure.oauthuserprovider;


import static go.alarm.global.response.ResponseCode.INVALID_AUTHORIZATION_CODE;
import static go.alarm.global.response.ResponseCode.NOT_SUPPORTED_OAUTH_SERVICE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import go.alarm.global.response.exception.AuthException;
import go.alarm.login.domain.OauthProvider;
import go.alarm.login.domain.OauthUserInfo;
import go.alarm.login.infrastructure.oauthuserinfo.AppleUserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.math.BigInteger;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.util.Base64;

@Component
public class AppleOauthProvider implements OauthProvider {

    private static final String PROPERTIES_PATH = "${oauth2.provider.apple.";
    // application.yml 파일에 설정을 정의하고, 이를 Spring 애플리케이션에서 주입받습니다
    private static final String PROVIDER_NAME = "apple";

    private final String clientId;
    private final String teamId;
    private final String keyId;
    private final String keyPath;

    private static final String APPLE_JWKS_URL = "https://appleid.apple.com/auth/keys";
    public AppleOauthProvider(
        @Value(PROPERTIES_PATH + "client.id}") final String clientId,
        @Value(PROPERTIES_PATH + "team.id}") final String teamId,
        @Value(PROPERTIES_PATH + "key.id}") final String keyId,
        @Value(PROPERTIES_PATH + "key.path}") final String keyPath
    ) {
        this.clientId = clientId;
        this.teamId = teamId;
        this.keyId = keyId;
        this.keyPath = keyPath;
    }

    @Override
    public boolean is(final String name) {
        return PROVIDER_NAME.equals(name);
    }

    @Override
    public OauthUserInfo getUserInfo(final String identityToken) {
        try {
            PublicKey publicKey = getPublicKey(identityToken);
            // identityToken의 서명을 확인하기 위해 Apple의 공개키를 가져옴

            Claims claims = Jwts.parserBuilder()// 토큰의 무결성과 신뢰성을 검증
                .setSigningKey(publicKey)// 앞서 획득한 공개키를 설정하여 토큰의 서명을 검증
                .build()
                .parseClaimsJws(identityToken)
                .getBody();

            String socialLoginId = claims.getSubject(); // subject(사용자의 고유 식별자) 추출
            String email = claims.get("email", String.class); // 이메일 추출

            return new AppleUserInfo(socialLoginId, email);
        } catch (Exception e) {
            throw new AuthException(INVALID_AUTHORIZATION_CODE);
        }
    }

    /**
     * Apple의 identityToken을 검증하기 위한 공개키를 획득합니다.
     * */
    private PublicKey getPublicKey(String identityToken) throws Exception {
        String kid = extractKidFromToken(identityToken);
        // 토큰의 헤더에서 kid(Key ID)를 추출

        Map<String, Object> jwksResponse = restTemplate.getForObject(APPLE_JWKS_URL, Map.class);
        // RestTemplate을 사용하여 Apple의 JWKS URL에 HTTP GET 요청, JWKS에는 Apple이 토큰 서명에 사용하는 여러 공개키가 포함됨

        if (jwksResponse == null || !jwksResponse.containsKey("keys")) {
            // JWKS가 유효하지 않거나 사용할 수 없는 경우 예외를 발생
            throw new AuthException(NOT_SUPPORTED_OAUTH_SERVICE);
        }

        List<Map<String, String>> keys = (List<Map<String, String>>) jwksResponse.get("keys");
        // JWKS에서 키 목록을 추출

        Map<String, String> key = keys.stream() // Java 스트림을 사용하여 일치하는 kid를 가진 키를 찾음
            .filter(k -> kid.equals(k.get("kid")))
            .findFirst()
            .orElseThrow(() -> new AuthException(NOT_SUPPORTED_OAUTH_SERVICE));

        String n = key.get("n");
        String e = key.get("e");
        // RSA 공개키의 모듈러스(n)와 지수(e)를 검색

        BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(n));
        BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(e));
        // Base64URL로 인코딩된 모듈러스(n)와 지수(e)를 디코딩

        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
        // 디코딩된 모듈러스와 지수를 사용하여 RSAPublicKeySpec을 생성, 이 명세는 RSA 공개키를 설명

        KeyFactory factory = KeyFactory.getInstance("RSA");
        // RSA KeyFactory 인스턴스를 얻음
        
        return factory.generatePublic(spec);
        // 팩토리를 사용하여 명세로부터 PublicKey 객체를 생성
    }

    /**
     * JWT의 헤더에서 'kid' (Key ID) 값을 추출합니다.
     * */
    private String extractKidFromToken(String identityToken) throws JsonProcessingException {
        String[] parts = identityToken.split("\\.");
        // split 메소드를 사용하여 토큰을 세 부분으로 나눔

        if (parts.length != 3) {
            // 유효한 JWT는 반드시 세 부분으로 구성되어야 하기 때문에 length가 3이 아니면 토큰이 유효하지 않다고 판단

            throw new AuthException(INVALID_AUTHORIZATION_CODE);
        }

        String header = new String(Base64.getUrlDecoder().decode(parts[0]));
        // 토큰의 첫 번째 부분(parts[0])이 헤더, 이를 디코딩

        Map<String, String> headerMap = new ObjectMapper().readValue(header, Map.class);
        // ObjectMapper를 사용하여 JSON 문자열을 Map 객체로 파싱

        return headerMap.get("kid");
    }
}