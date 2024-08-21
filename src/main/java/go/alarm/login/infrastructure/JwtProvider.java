package go.alarm.login.infrastructure;



import static go.alarm.global.response.ResponseCode.EXPIRED_PERIOD_ACCESS_TOKEN;
import static go.alarm.global.response.ResponseCode.EXPIRED_PERIOD_REFRESH_TOKEN;
import static go.alarm.global.response.ResponseCode.INVALID_ACCESS_TOKEN;
import static go.alarm.global.response.ResponseCode.INVALID_REFRESH_TOKEN;

import go.alarm.global.response.exception.ExpiredPeriodJwtException;
import go.alarm.global.response.exception.InvalidJwtException;
import go.alarm.login.domain.UserTokens;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
* JWT 토큰을 생성하고 검증
* */
@Component
public class JwtProvider {

    public static final String EMPTY_SUBJECT = "";

    private final SecretKey secretKey;
    private final Long accessExpirationTime;
    private final Long refreshExpirationTime;

    public JwtProvider(
        @Value("${security.jwt.secret-key}") final String secretKey,
        @Value("${security.jwt.access-expiration-time}") final Long accessExpirationTime,
        @Value("${security.jwt.refresh-expiration-time}") final Long refreshExpirationTime
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        // HMAC-SHA 알고리즘에 적합한 키를 생성

        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
    }

    /**
    * 로그인 시 사용자 식별자(subject)를 받아 액세스 토큰과 리프레시 토큰을 생성
    * */
    public UserTokens generateLoginToken(final String subject) {

        final String refreshToken = createToken(EMPTY_SUBJECT, refreshExpirationTime);
        // 보안 강화를 위해 리프레시 토큰에는 subject(사용자 식별자)를 포함하지 않고, EMPTY_SUBJECT를 넣어준다.

        final String accessToken = createToken(subject, accessExpirationTime);
        return new UserTokens(refreshToken, accessToken);
    }

    /**
     * 새로운 액세스 토큰을 생성
     * */
    public String regenerateAccessToken(final String subject) {
        return createToken(subject, accessExpirationTime);
    }

    /**
    * 실제 JWT 토큰을 생성하는 메서드
    * */
    private String createToken(final String subject, final Long validityInMilliseconds) {
        final Date now = new Date();
        final Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
            // 헤더 설정
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 토큰의 타입을 JWT로 설정
            
            // 페이로드 설정
            .setSubject(subject) // 사용자 식별자를 설정
            .setIssuedAt(now) // 토큰 발행 시간을 설정
            .setExpiration(validity) // 토큰 만료 시간을 설정
            
            // 서명
            .signWith(secretKey, SignatureAlgorithm.HS256) // secretKey를 사용하여 HS256 알고리즘으로 토큰에 서명
            
            // 토큰 생성
            .compact(); // 최종적으로 JWT 문자열 토큰을 생성
    }

    /**
     * 액세스 토큰과 리프레시 토큰의 유효성을 검사
     * */
    public void validateTokens(final UserTokens userTokens) {
        validateRefreshToken(userTokens.getRefreshToken());
        validateAccessToken(userTokens.getAccessToken());
    }


    /**
     * 리프레시 토큰 유효성 검사
     * */
    private void validateRefreshToken(final String refreshToken) {
        try {
            parseToken(refreshToken);
        } catch (final ExpiredJwtException e) {
            throw new ExpiredPeriodJwtException(EXPIRED_PERIOD_REFRESH_TOKEN);
        } catch (final JwtException | IllegalArgumentException e) {
            throw new InvalidJwtException(INVALID_REFRESH_TOKEN);
        }
    }

    /**
     * 엑세스 토큰 유효성 검사
     * */
    private void validateAccessToken(final String accessToken) {
        try {
            parseToken(accessToken);
        } catch (final ExpiredJwtException e) {
            throw new ExpiredPeriodJwtException(EXPIRED_PERIOD_ACCESS_TOKEN); // 만료된 토큰 에러
        } catch (final JwtException | IllegalArgumentException e) {
            throw new InvalidJwtException(INVALID_ACCESS_TOKEN); // 잘못된 토큰 에러
        }
    }

    /**
     * 토큰에서 subject(사용자 식별자)를 추출
     * */
    public String getSubject(final String token) {
        return parseToken(token)
            .getBody()
            .getSubject();
    }

    /**
     * 주어진 JWT 토큰을 파싱하고 검증
     * */
    private Jws<Claims> parseToken(final String token) {
        // Claims는 JWT의 페이로드에 포함된 클레임(정보)들을 나타냅니다.

        return Jwts.parserBuilder()
            .setSigningKey(secretKey) // 토큰 생성 시 사용한 것과 동일한 비밀 키를 설정
            .build()
            .parseClaimsJws(token); // 주어진 토큰을 파싱하고 서명을 검증
    }



    /**
     * 리프레시 토큰은 유효하고 액세스 토큰은 만료된 상황인지 확인
     * */
    public boolean isValidRefreshAndInvalidAccess(final String refreshToken, final String accessToken) {
        validateRefreshToken(refreshToken);
        try {
            validateAccessToken(accessToken);
        } catch (final ExpiredPeriodJwtException e) {
            return true;
        }
        return false;
    }

    /**
     * 리프레시 토큰은 유효하고 액세스 토큰도 유효한 상황인지 확인
     * */
    public boolean isValidRefreshAndValidAccess(final String refreshToken, final String accessToken) {
        try {
            validateRefreshToken(refreshToken);
            validateAccessToken(accessToken);
            return true;
        } catch (final JwtException e) {
            return false;
        }
    }
}
