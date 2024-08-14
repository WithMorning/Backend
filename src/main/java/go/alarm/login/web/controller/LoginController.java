package go.alarm.login.web.controller;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import go.alarm.login.domain.UserTokens;
import go.alarm.login.dto.AccessToken;
import go.alarm.login.web.converter.LoginConverter;
import go.alarm.response.BaseResponse;
import go.alarm.login.service.LoginService;
import go.alarm.login.dto.LoginRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class LoginController {

    private final LoginService loginService;
    public static final int COOKIE_AGE_SECONDS = 604800;

    /**
    * 소셜로그인 API입니다.
    * 현재는 애플 소셜로그인만 구현했습니다.
    * 추후 Resource Server가 추가될 수도 있기에 {provider}로 받아옵니다.
    * */
    @PostMapping("/login/oauth/{provider}")
    public BaseResponse<AccessToken> login(
        @PathVariable String provider,
        @RequestBody LoginRequest request,
        final HttpServletResponse response
    ) {

        final UserTokens userTokens = loginService.login(provider, request.getCode());
        // userTokens의 필드 -> refreshToken, accessToken

        final ResponseCookie cookie = ResponseCookie.from("refresh-token", userTokens.getRefreshToken())
            .maxAge(COOKIE_AGE_SECONDS) // 쿠키의 유효 시간  설정
            .sameSite("None") // 크로스 사이트 요청에 대한 쿠키 전송 정책을 설정, "None"으로 설정하면 모든 크로스 사이트 컨텍스트에서 쿠키를 전송 가능
            .secure(true) // HTTPS 연결에서만 쿠키를 전송하도록 설정
            .httpOnly(true) // JavaScript를 통한 쿠키 접근을 방지하여 보안을 강화
            .path("/") // 쿠키가 유효한 경로를 설정, "/"로 설정하면 전체 사이트에서 쿠키가 유효
            .build();

        response.addHeader(SET_COOKIE, cookie.toString());
        // 생성한 쿠키를 HTTP 응답 헤더에 추가
        // response.addHeader() 메서드는 HttpServletResponse 객체를 직접 조작하므로, return 문의 형식과 관계없이 응답 헤더에 포함

        return new BaseResponse<>(LoginConverter.toAccessToken(userTokens.getAccessToken()));

        //return new BaseResponse<>(new AccessToken(userTokens.getAccessToken()));
        //위처럼 해도 될지 테스트 해봐야 함.
    }

    /*
    @PostMapping("/token")
    public ResponseEntity<AccessTokenResponse> extendLogin(
        @CookieValue("refresh-token") final String refreshToken,
        @RequestHeader("Authorization") final String authorizationHeader // Authorization 헤더 값 (Bearer 토큰을 포함)
    ) {
        final String renewalRefreshToken = loginService.renewalAccessToken(refreshToken, authorizationHeader);
        return ResponseEntity.status(CREATED).body(new AccessTokenResponse(renewalRefreshToken));
    }
    */

    /*
    @DeleteMapping("/logout")
    @MemberOnly
    public ResponseEntity<Void> logout(
        @Auth final Accessor accessor,
        @CookieValue("refresh-token") final String refreshToken) {
        loginService.removeRefreshToken(refreshToken);
        return ResponseEntity.noContent().build();
    }
    */

    /*
    일단 삭제는 보류
    @DeleteMapping("/account")
    @MemberOnly
    public ResponseEntity<Void> deleteAccount(@Auth final Accessor accessor) {
        loginService.deleteAccount(accessor.getMemberId());
        return ResponseEntity.noContent().build();
    }*/

}
