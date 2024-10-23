package go.alarm.login.infrastructure.oauthuserprovider;


import static go.alarm.global.response.ResponseCode.FAIL_REVOKE_APPLE_TOKEN;
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
import io.jsonwebtoken.SignatureAlgorithm;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.util.Base64;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
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
        this.clientId = clientId; //  Apple Developer Console에서 확인할 수 있는 클라이언트 ID
        this.teamId = teamId; // Apple Developer 팀 ID
        this.keyId = keyId; // Apple Developer Console에서 생성한 private key의 ID
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

    @Override
    public void revokeToken(String socialLoginId) {
        try {

            String clientSecret = generateAppleClientSecret();

            // Apple Revoke Token API 호출을 위한 파라미터
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("token", socialLoginId);

            // API 호출
            RestTemplate restTemplate = new RestTemplate();
            String revokeUrl = "https://appleid.apple.com/auth/revoke";

            ResponseEntity<String> response = restTemplate.postForEntity(
                revokeUrl,
                params,
                String.class
            );
            /**
             * 사용 과정을 순서대로 보면:
             * 앱 서버가 Apple API를 호출하려고 함
             * generateAppleClientSecret()메소드로 Client Secret JWT를 생성 (iss, iat, exp, aud, sub 정보 + private key로 서명)
             * API 요청 시 이 JWT를 client_secret 파라미터로 전달
             * Apple 서버는 JWT를 검증하고, 이 요청이 진짜 해당 앱의 서버에서 온 것인지 확인
             * 검증이 성공하면 요청한 작업(토큰 취소 등)을 수행
             * */

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new AuthException(FAIL_REVOKE_APPLE_TOKEN);
            }
        } catch (Exception e) {
            // 예상치 못한 에러의 상세 내용 로깅
            log.error("애플 토큰 삭제 중 예상치 못한 에러 발생", e);
            // 표준화된 에러 응답 전달
            throw new AuthException(FAIL_REVOKE_APPLE_TOKEN);
        }
    }


    /**
     *  Apple Client Secret 생성하는 메소드입니다. 이는 서버가 애플 서버에 API 요청을 할 때 자신을 인증하기 위한 용도로 JWT 형태로 생성합니다.
     *  Client Secret은 서버가 자신을 인증하는 "신분증" 같은 역할을 한다고 보면 됩니다.
     * */
    private String generateAppleClientSecret() {
        try {
            // private key 파일 읽기
            byte[] privateKeyBytes = Files.readAllBytes(Path.of(keyPath));

            // PEM 형식의 헤더/푸터를 제거하고 공백을 제거하여 순수 키 내용만 추출
            String privateKeyContent = new String(privateKeyBytes)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

            // PKCS8 형식의 private key 생성
            byte[] decodedKey = Base64.getDecoder().decode(privateKeyContent); // Base64로 인코딩된 키를 디코드
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey); // PKCS8 형식의 키 스펙을 생성
            KeyFactory keyFactory = KeyFactory.getInstance("EC"); // EC 알고리즘을 사용하는 KeyFactory를 생성
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec); // KeyFactory를 통해 실제 프라이빗 키 객체를 생성

            // JWT 생성 시간과 만료 시간 설정 (만료시간은 생성 후 5분)
            LocalDateTime now = LocalDateTime.now();
            Date issuedAt = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
            Date expirationTime = Date.from(
                now.plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant()
            );

            // JWT 생성
            return Jwts.builder()
                .setHeaderParam("kid", keyId)
                .setHeaderParam("alg", "ES256")       // 서명 알고리즘
                .setIssuer(teamId)                          // 발급자 (Apple Developer Team ID)
                .setIssuedAt(issuedAt)                      // 발급 시간
                .setExpiration(expirationTime)              // 만료 시간
                .setAudience("https://appleid.apple.com")   // 대상자
                .setSubject(clientId)
                .signWith(privateKey, SignatureAlgorithm.ES256) // ES256으로 서명
                .compact();

        } catch (Exception e) {
            //throw new AuthException(FAIL_REVOKE_APPLE_TOKEN);
            log.warn("Apple Client Secret을 생성하던 도중에 에러 발생", e);
            throw new AuthException(FAIL_REVOKE_APPLE_TOKEN);
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