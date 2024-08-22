package go.alarm.login.presentation;


import go.alarm.auth.Auth;
import go.alarm.auth.domain.Accessor;
import go.alarm.login.domain.UserTokens;
import go.alarm.global.response.SuccessResponse;
import go.alarm.login.dto.RefreshTokenRequest;
import go.alarm.login.dto.LoginTokensResponse;
import go.alarm.login.service.LoginService;
import go.alarm.login.dto.LoginRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class LoginController {

    private final LoginService loginService;

    /**
    * 소셜로그인 API입니다.
    * 현재는 애플 소셜로그인만 구현했습니다.
    * 추후 Resource Server가 추가될 수도 있기에 {provider}로 받아옵니다.
    * */
    @PostMapping("/login/oauth/{provider}")
    public SuccessResponse<LoginTokensResponse> login(
        @PathVariable final String provider,
        @RequestBody final LoginRequest request,
        final HttpServletResponse response
    ) {
        final UserTokens userTokens = loginService.login(provider, request.getCode());
        // userTokens의 필드 -> refreshToken, accessToken
        log.warn("컨트롤러단(/login) 리프레시 토큰 >>" + userTokens.getRefreshToken());
        log.warn("컨트롤러단(/login) 엑세스 토큰 >>" + userTokens.getAccessToken());

        return new SuccessResponse<>(new LoginTokensResponse(userTokens.getAccessToken(), userTokens.getRefreshToken()));
    }



    @PostMapping("/accesstoken")
    public SuccessResponse<LoginTokensResponse> extendLogin(
        @RequestBody final RefreshTokenRequest request,
        @RequestHeader("Authorization") final String authorizationHeader // Authorization 헤더 값 (Bearer 토큰을 포함한 엑세스 토큰)
    ) {
        log.warn("컨트롤러단(/accesstoken) 리프레시 토큰 >>" + request.getRefreshToken());
        log.warn("컨트롤러단(/accesstoken) 엑세스 토큰 >>" + authorizationHeader);
        final String renewalAccessToken = loginService.renewalAccessToken(request.getRefreshToken(), authorizationHeader);
        return new SuccessResponse<>(new LoginTokensResponse(renewalAccessToken));
    }



    @DeleteMapping("/logout")
    public SuccessResponse<Void> logout(
        @Auth final Accessor accessor,
        @RequestBody final RefreshTokenRequest request) {
        loginService.removeRefreshToken(request.getRefreshToken());
        return new SuccessResponse<>();

    }


    /*
    일단 삭제는 보류
    @DeleteMapping("/account")
    @MemberOnly
    public ResponseEntity<Void> deleteAccount(@Auth final Accessor accessor) {
        loginService.deleteAccount(accessor.getMemberId());
        return ResponseEntity.noContent().build();
    }*/

}
