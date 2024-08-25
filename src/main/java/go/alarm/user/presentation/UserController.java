package go.alarm.user.presentation;

import go.alarm.auth.Auth;
import go.alarm.auth.UesrOnly;
import go.alarm.auth.domain.Accessor;
import go.alarm.user.domain.User;
import go.alarm.global.response.SuccessResponse;
import go.alarm.user.dto.response.MyPageResponse;
import go.alarm.user.service.UserService;
import go.alarm.user.dto.request.UserBedTimeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/mypage")
    @UesrOnly
    @Operation(summary = "마이페이지 조회 API", description = "마이페이지를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "요청에 성공하였습니다.")
    })
    public SuccessResponse<MyPageResponse> getMyPage(
        @Auth final Accessor accessor) {

        User user = userService.getUser(accessor.getUserId());

        return new SuccessResponse<>(MyPageConverter.getMyPage(user));
    }

    @PostMapping("/bedtime/alarm")
    @UesrOnly
    @Operation(summary = "취침알림 시간 및 요일 설정 API", description = "취침알림 시간 및 요일을 설정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "요청에 성공하였습니다.")
    })
    public SuccessResponse<Void> setBedTime(
        @Auth final Accessor accessor,
        @RequestBody @Valid UserBedTimeRequest request) {
        //소셜로그인이 들어가면 위 헤더 부분이 사라지고 토큰으로 유저를 구분해야함.

        userService.setBedTime(accessor.getUserId(), request);

        return new SuccessResponse<>();
    }
}
