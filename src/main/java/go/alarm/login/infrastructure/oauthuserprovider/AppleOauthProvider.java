package go.alarm.login.infrastructure.oauthuserprovider;


import static go.alarm.global.response.ResponseCode.FAIL_GET_APPLE_TOKEN;
import static go.alarm.global.response.ResponseCode.FAIL_REVOKE_APPLE_TOKEN;
import static go.alarm.global.response.ResponseCode.INVALID_AUTHORIZATION_CODE;
import static go.alarm.global.response.ResponseCode.NOT_FOUND_AUTHORIZATION_CODE;
import static go.alarm.global.response.ResponseCode.NOT_FOUND_CLIENT_SECRET;
import static go.alarm.global.response.ResponseCode.NOT_FOUND_REFRESH_TOKEN;
import static go.alarm.global.response.ResponseCode.NOT_SUPPORTED_OAUTH_SERVICE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import go.alarm.global.response.exception.AuthException;
import go.alarm.login.domain.OauthProvider;
import go.alarm.login.domain.OauthUserInfo;
import go.alarm.login.infrastructure.oauthuserinfo.AppleUserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.util.Base64;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
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
    private final String redirectUri;

    private static final String APPLE_JWKS_URL = "https://appleid.apple.com/auth/keys";
    public AppleOauthProvider(
        @Value(PROPERTIES_PATH + "client.id}") final String clientId,
        @Value(PROPERTIES_PATH + "team.id}") final String teamId,
        @Value(PROPERTIES_PATH + "key.id}") final String keyId,
        @Value(PROPERTIES_PATH + "key.path}") final String keyPath,
        @Value(PROPERTIES_PATH + "redirect.url}") final String redirectUri
    ) {
        this.clientId = clientId; //  Apple Developer Console에서 확인할 수 있는 identifier
        this.teamId = teamId; // Apple Developer 팀 ID
        this.keyId = keyId; // Apple Developer Console에서 생성한 private key의 ID
        this.keyPath = keyPath;
        this.redirectUri = redirectUri;
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
                .setSigningKey(publicKey)       // 앞서 획득한 공개키를 설정하여 토큰의 서명을 검증
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
    public String getRefreshToken(final String authorizationCode) throws IOException {
        String tokenUrl = "https://appleid.apple.com/auth/token";

        // 파라미터 유효성 검사 추가
        if (authorizationCode == null || authorizationCode.trim().isEmpty()) {
            throw new AuthException(NOT_FOUND_AUTHORIZATION_CODE);
        }

        // Apple Login 필수 파라미터 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("client_id", clientId);  // 애플 Developer에서 받은 Bundle ID를 사용

        // client_secret 생성 전 검증
        String clientSecret = generateAppleClientSecret();
        if (clientSecret == null || clientSecret.trim().isEmpty()) {
            throw new AuthException(NOT_FOUND_CLIENT_SECRET);
        }
        params.add("client_secret", generateAppleClientSecret());  // JWT 형식의 client secret

        params.add("code", authorizationCode);
        params.add("grant_type", "authorization_code");

        // redirect_uri가 Apple Developer에 등록된 것과 정확히 일치하는지 확인
        if (redirectUri != null && !redirectUri.trim().isEmpty()) {
            params.add("redirect_uri", redirectUri);
        }
        else {
            throw new AuthException(NOT_FOUND_CLIENT_SECRET);
        }

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // HTTP 요청 설정
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            // RestTemplate으로 토큰 요청
            ResponseEntity<Map> response = restTemplate.postForEntity(
                tokenUrl,
                request,
                Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String refreshToken = (String) response.getBody().get("refresh_token");

                if (refreshToken == null || refreshToken.trim().isEmpty()) {
                    throw new AuthException(NOT_FOUND_REFRESH_TOKEN);
                }
                return refreshToken;
            }

            //log.error("Failed to get Apple refresh token. Response: {}", response);
            throw new AuthException(FAIL_GET_APPLE_TOKEN);
        }catch (HttpClientErrorException e) {
            throw new AuthException(FAIL_GET_APPLE_TOKEN, e);
        }
        catch (Exception e) {
            //log.error("Error while getting Apple refresh token", e);
            throw new AuthException(FAIL_GET_APPLE_TOKEN, e);
        }
    }

    @Override
    public Boolean revokeToken(Long userId, String appleRefreshToken) {
        /**
         * 사용 과정을 순서대로 보면:
         * 앱 서버가 Apple API를 호출하려고 함
         * generateAppleClientSecret()메소드로 Client Secret JWT를 생성 (iss, iat, exp, aud, sub 정보 + private key로 서명)
         * API 요청 시 이 JWT를 client_secret 파라미터로 전달
         * Apple 서버는 JWT를 검증하고, 이 요청이 진짜 해당 앱의 서버에서 온 것인지 확인
         * 검증이 성공하면 요청한 작업(토큰 취소 등)을 수행
         * */

        try {
            String clientSecret = generateAppleClientSecret();

            // Apple Revoke Token API 호출을 위한 파라미터
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("token", appleRefreshToken);             // apple refresh token 사용
            params.add("token_type_hint", "refresh_token");     // refresh token임을 명시

            // API 호출
            RestTemplate restTemplate = new RestTemplate();
            String revokeUrl = "https://appleid.apple.com/auth/revoke";

            ResponseEntity<String> response = restTemplate.postForEntity(
                revokeUrl,
                params,
                String.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("애플 토큰 삭제 실패. status: {}, response: {}",
                    response.getStatusCode(), response.getBody());
                throw new AuthException(FAIL_REVOKE_APPLE_TOKEN);
            }
            log.info("애플 토큰 삭제 성공. userId: {}", userId);
            return true;

        } catch (Exception e) {
            log.error("애플 토큰 삭제 중 예상치 못한 에러 발생. userId: {}", userId, e);
            throw new AuthException(FAIL_REVOKE_APPLE_TOKEN);
        }
    }

    /**
     *  Apple Client Secret 생성하는 메소드입니다. 이는 서버가 애플 서버에 API 요청을 할 때 자신을 인증하기 위한 용도로 JWT 형태로 생성합니다.
     *  Client Secret은 서버가 자신을 인증하는 "신분증" 같은 역할을 한다고 보면 됩니다.
     * */
    private String generateAppleClientSecret() throws IOException {
        Date expirationDate = Date.from(LocalDateTime.now()
            .plusDays(5)
            .atZone(ZoneId.systemDefault())
            .toInstant());

        Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("kid", keyId);
        jwtHeader.put("alg", "ES256");

        return Jwts.builder()
            .setHeaderParams(jwtHeader)
            .setIssuer(teamId)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(expirationDate)
            .setAudience("https://appleid.apple.com")
            .setSubject(clientId)
            .signWith(getPrivateKey())
            .compact();
    }

    // 키 파일 로딩 및 변환 담당 메소드
    private PrivateKey getPrivateKey() throws IOException {
        File keyFile = new File(keyPath);
        try (FileInputStream inputStream = new FileInputStream(keyFile)) {
            String privateKeyContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            PEMParser pemParser = new PEMParser(new StringReader(privateKeyContent));
            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();

            return new JcaPEMKeyConverter()
                .getPrivateKey(privateKeyInfo);
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