package go.alarm.login;

import static go.alarm.global.response.ResponseCode.INVALID_REFRESH_TOKEN;
import static go.alarm.global.response.ResponseCode.INVALID_REQUEST;
import static go.alarm.global.response.ResponseCode.INVALID_USER;
import static go.alarm.global.response.ResponseCode.NOT_FOUND_REFRESH_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.fasterxml.jackson.databind.ObjectMapper;
import go.alarm.auth.Auth;
import go.alarm.auth.domain.Accessor;
import go.alarm.global.response.exception.BadRequestException;
import go.alarm.global.response.exception.RefreshTokenException;
import go.alarm.login.domain.UserTokens;
import go.alarm.login.domain.repository.RefreshTokenRepository;
import go.alarm.login.dto.RefreshTokenRequest;
import go.alarm.login.infrastructure.BearerAuthorizationExtractor;
import go.alarm.login.infrastructure.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 *  @Auth 어노테이션이 붙은 Long 타입 파라미터에 대해 자동으로 사용자 인증 처리를 할 수 있도록 해줍니다.
 *  컨트롤러에서 매번 인증 로직을 작성할 필요 없이, 간단히 @Auth Long userId와 같은 형태로 인증된 사용자의 ID를 받아올 수 있습니다.
 * */
@RequiredArgsConstructor
@Component
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtProvider jwtProvider;
    private final BearerAuthorizationExtractor extractor;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ObjectMapper objectMapper;

    /**
     * 이 loginArgumentResolver가 처리할 수 있는 parameter인지 확인합니다.
     * 이 메소드가 true를 반환할 때만 resolveArgument 메소드가 호출됩니다.
     * */
    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.withContainingClass(Long.class) // 파라미터 타입이 Long인지 확인
            .hasParameterAnnotation(Auth.class); // @Auth 어노테이션이 있는지 확인
    }

    /**
     * 실제로 파라미터 값을 해석하고 반환합니다.
     * */
    @Override
    public Accessor resolveArgument(
        final MethodParameter parameter,
        final ModelAndViewContainer mavContainer,
        final NativeWebRequest webRequest,
        final WebDataBinderFactory binderFactory
    ) {
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new BadRequestException(INVALID_REQUEST);
        }

        try {
            final String refreshToken = extractRefreshToken(request);
            final String accessToken = extractor.extractAccessToken(webRequest.getHeader(AUTHORIZATION));
            jwtProvider.validateTokens(new UserTokens(refreshToken, accessToken));

            final Long userId = Long.valueOf(jwtProvider.getSubject(accessToken)); // 액세스 토큰에서 사용자 ID를 추출
            return Accessor.member(userId); // 인증된 사용자 객체를 반환

        } catch (final RefreshTokenException e) {
            throw new RefreshTokenException(INVALID_USER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 리프레시 토큰의 존재 여부와 유효성을 검사합니다.
     * */
    private String extractRefreshToken(final HttpServletRequest request) throws IOException {

        String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        // request.getReader().lines()를 사용하여 HTTP 요청의 본문을 읽음
        // .collect를 통해 모든 라인을 하나의 문자열로 결합

        RefreshTokenRequest refreshTokenRequest = objectMapper.readValue(body, RefreshTokenRequest.class);
        // JSON 문자열을 RefreshTokenRequest 객체로 변환

        String refreshToken = refreshTokenRequest.getRefreshToken();

        if (refreshToken == null || refreshToken.isEmpty()) { // 토큰이 null이거나 빈 문자열인지 확인
            throw new RefreshTokenException(NOT_FOUND_REFRESH_TOKEN);
        }

        if (!refreshTokenRepository.existsById(refreshToken)) { // 토큰 유효성 검사
            throw new RefreshTokenException(INVALID_REFRESH_TOKEN);
        }
        return refreshToken;
    }
}