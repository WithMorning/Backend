package go.alarm.web.controller;


import go.alarm.response.BaseResponse;
import go.alarm.service.LoginService;
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

        return new BaseResponse<>(GroupConverter.toCreateGroupDTO(group));

    }

}
