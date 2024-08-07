package go.alarm.login.controller;


import go.alarm.login.domain.MemberTokens;
import go.alarm.response.BaseResponse;
import go.alarm.login.service.LoginService;
import go.alarm.web.converter.GroupConverter;
import go.alarm.web.dto.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /*
    * 소셜로그인 API
    * 현재는 애플 소셜로그인만 구현했습니다.
    * 추후 Resource Server가 추가될 수도 있기에 {provider}로 받아옵니다.
    * */
    @PostMapping("/login/oauth/{provider}")
    public BaseResponse<Object> login(
        @PathVariable String provider,
        @RequestBody LoginRequest request) {

        MemberTokens memberTokens = loginService.login(provider, request.getCode());

        //return ResponseEntity.status(CREATED).body(new AccessTokenResponse(memberTokens.getAccessToken())); 기존 코드

        //빌더 패턴으로 엑세스 토큰하고, 유저 id 던져주면 될듯?
        return new BaseResponse<>(GroupConverter.toCreateGroupDTO(group));// 여기 수정해야 함.

    }

}
