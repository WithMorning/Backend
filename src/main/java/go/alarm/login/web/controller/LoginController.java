package go.alarm.login.web.controller;

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
            .maxAge(COOKIE_AGE_SECONDS)
            .sameSite("None")
            .secure(true)
            .httpOnly(true)
            .path("/")
            .build();

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
