package go.alarm.web.controller;

import go.alarm.domain.entity.User;
import go.alarm.response.BaseResponse;
import go.alarm.service.UserService;
import go.alarm.web.converter.UserConverter;
import go.alarm.web.dto.request.UserRequestDTO;
import go.alarm.web.dto.response.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/bedtime/alarm")
    @Operation(summary = "취침알림 시간 및 요일 설정 API", description = "취침알림 시간 및 요일을 설정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "요청에 성공하였습니다.")
    })
    @Parameters({
        @Parameter(name = "userId", description = "유저의 아이디, header에 담아주시면 됩니다.")
    })
    public BaseResponse<UserResponseDTO.setUserBedTimeDTO> setBedTime(@RequestHeader(name = "userId") Long userId,
                                                @RequestBody @Valid UserRequestDTO.SetBedTimeDTO request) {
        //소셜로그인이 들어가면 위 헤더 부분이 사라지고 토큰으로 유저를 구분해야함.

        User user = userService.setBedTime(userId, request);

        return new BaseResponse<>(UserConverter.setBedTime(user));
    }

}
