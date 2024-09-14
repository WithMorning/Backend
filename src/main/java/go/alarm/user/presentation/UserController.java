package go.alarm.user.presentation;

import go.alarm.auth.Auth;
import go.alarm.auth.UesrOnly;
import go.alarm.auth.domain.Accessor;
import go.alarm.user.domain.User;
import go.alarm.global.response.SuccessResponse;
import go.alarm.user.dto.request.CodeVerificationRequest;
import go.alarm.user.dto.request.DisturbBanModeRequest;
import go.alarm.user.dto.request.PhoneSmsRequest;
import go.alarm.user.dto.request.UserProfileRequest;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
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

        userService.setBedTime(accessor.getUserId(), request);
        return new SuccessResponse<>();
    }

    @PostMapping(value = "/profile", consumes = "multipart/form-data")
    @UesrOnly
    @Operation(summary = "유저 프로필 설정 API", description = "유저의 프로필 사진, 닉네임을 설정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청에 성공하였습니다.")
    })
    public SuccessResponse<Void> setProfile(
        @Auth final Accessor accessor,
        @RequestPart @Valid UserProfileRequest request) {

        userService.setProfile(accessor.getUserId(), request);
        return new SuccessResponse<>();
    }

    @PostMapping(value = "/send-code")
    @UesrOnly
    @Operation(summary = "휴대폰 문자 인증 요청 API", description = "휴대폰 인증 문자를 보냅니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청에 성공하였습니다.")
    })
    public SuccessResponse<Void> sendVerificationCode(
        @Auth final Accessor accessor,
        @RequestBody @Valid PhoneSmsRequest request) {

        userService.sendVerificationCode(request.getPhone());

        return new SuccessResponse<>();
    }

    @PostMapping(value = "/verify-code")
    @UesrOnly
    @Operation(summary = "휴대폰 문자 인증 확인 API", description = "휴대폰 인증 문자를 확인합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청에 성공하였습니다.")
    })
    public SuccessResponse<Void> verifyCode(
        @Auth final Accessor accessor,
        @RequestBody @Valid CodeVerificationRequest request) {

        userService.verifyCode(request.getPhone(), request.getCode(), accessor.getUserId());

        return new SuccessResponse<>();
    }

    @PatchMapping(value = "/{groupId}/disturb")
    @UesrOnly
    @Operation(summary = "방해금지모드 설정 API", description = "특정 그룹의 방해금지모드를 On/Off 합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청에 성공하였습니다.")
    })
    public SuccessResponse<Void> setDisturbBanMode(
        @Auth final Accessor accessor,
        @PathVariable(name = "groupId") Long groupId,
        @RequestBody @Valid DisturbBanModeRequest request) {

        userService.setDisturbBanMode(accessor.getUserId(), groupId, request.getIsDisturbBanMode());

        return new SuccessResponse<>();
    }

    @PatchMapping(value = "/{groupId}/wake-status")
    @UesrOnly
    @Operation(summary = "기상 상태로 변경 API", description = "특정 그룹의 유저 상태를 기상 상태로 변경합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청에 성공하였습니다.")
    })
    public SuccessResponse<Void> setWakeStatus(
        @Auth final Accessor accessor,
        @PathVariable(name = "groupId") Long groupId) {

        userService.setWakeStatus(accessor.getUserId(), groupId);

        return new SuccessResponse<>();
    }


}
