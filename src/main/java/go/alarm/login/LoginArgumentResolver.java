package go.alarm.login;

import static go.alarm.global.response.ResponseCode.INVALID_REFRESH_TOKEN;
import static go.alarm.global.response.ResponseCode.INVALID_REQUEST;
import static go.alarm.global.response.ResponseCode.INVALID_USER;
import static go.alarm.global.response.ResponseCode.NOT_FOUND_REFRESH_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import go.alarm.auth.Auth;
import go.alarm.auth.domain.Accessor;
import go.alarm.global.response.exception.BadRequestException;
import go.alarm.global.response.exception.RefreshTokenException;
import go.alarm.login.domain.RefreshToken;
import go.alarm.login.domain.repository.RefreshTokenRepository;
import go.alarm.login.infrastructure.BearerAuthorizationExtractor;
import go.alarm.login.infrastructure.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 *  @Auth 어노테이션이 붙은 파라미터에 대해 자동으로 사용자 인증 처리를 할 수 있도록 해줍니다.
 *  컨트롤러에서 매번 인증 로직을 작성할 필요 없이, 간단히 @Auth final Accessor accessor와 같은 형태로 인증된 사용자를 받아올 수 있습니다.
 *  원리는 resolveArgument 메서드가 반환한 Accessor 객체가 컨트롤러의 파라미터로 전달됩니다.
 * */
@RequiredArgsConstructor
@Component
@Slf4j
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtProvider jwtProvider;
    private final BearerAuthorizationExtractor extractor;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 이 loginArgumentResolver가 처리할 수 있는 parameter인지 확인합니다.
     * 이 메소드가 true를 반환할 때만 resolveArgument 메소드가 호출됩니다.
     * */
    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class); // @Auth 어노테이션이 있는지 확인
    }

    /**
     * 실제로 파라미터 값을 해석하고 Accessor 객체를 반환합니다.
     * */
    @Override
    public Accessor resolveArgument(
        final MethodParameter parameter, // 현재 처리 중인 컨트롤러 메소드의 파라미터에 대한 정보를 포함, 파라미터의 타입, 이름, 어노테이션 등의 메타데이터에 접근 가능, supportsParameter 메소드에서 이미 확인
        final ModelAndViewContainer mavContainer, // 컨트롤러의 처리 결과를 담는 컨테이너, 주로 뷰 렌더링과 관련된 작업에서 사용
        final NativeWebRequest webRequest, // 현재 웹 요청에 대한 추상화된 인터페이스, HTTP 요청의 파라미터, 헤더, 세션 등에 접근할 수 있음
        final WebDataBinderFactory binderFactory // WebDataBinder 인스턴스를 생성하는 팩토리, 요청 파라미터를 객체에 바인딩하는 데 사용
    ) {
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        log.warn("resolveArgument의 requset = webRequest.getNativeRequest: " + request.getHeader(AUTHORIZATION));
        if (request == null) {
            throw new BadRequestException(INVALID_REQUEST);
        }

        try {
            final String accessToken = extractor.extractAccessToken(webRequest.getHeader(AUTHORIZATION));
            jwtProvider.validateAccessToken(accessToken);

            final Long userId = Long.valueOf(jwtProvider.getSubject(accessToken)); // 액세스 토큰에서 사용자 ID를 추출

            final String refreshToken = extractRefreshToken(userId); // userId로 리프레시 토큰 추출
            jwtProvider.validateRefreshToken(refreshToken);

            return Accessor.user(userId); // 인증된 사용자 객체를 반환

        } catch (final RefreshTokenException e) {
            throw new RefreshTokenException(INVALID_USER);
        }
    }

    /**
     * 리프레시 토큰의 존재 여부와 유효성을 검사하여 리프레시 토큰을 추출해줍니다.
     * */
    private String extractRefreshToken(final Long userId){

        RefreshToken refreshTokenObject = refreshTokenRepository.findByUserId(userId);

        String refreshToken = refreshTokenObject.getRefreshToken();

        if (refreshToken == null || refreshToken.isEmpty()) { // 토큰이 null이거나 빈 문자열인지 확인
            throw new RefreshTokenException(NOT_FOUND_REFRESH_TOKEN);
        }

        if (!refreshTokenRepository.existsById(refreshToken)) { // 토큰 유효성 검사
            throw new RefreshTokenException(INVALID_REFRESH_TOKEN);
        }
        return refreshToken;
    }
}