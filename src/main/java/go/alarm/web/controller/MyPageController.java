package go.alarm.web.controller;

import static go.alarm.web.dto.response.MyPageResponseDTO.*;

import go.alarm.domain.entity.User;
import go.alarm.global.response.SuccessResponse;
import go.alarm.service.UserService;
import go.alarm.web.converter.MyPageConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/mypage")
@Slf4j
public class MyPageController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "마이페이지 조회 API", description = "마이페이지를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "요청에 성공하였습니다.")
    })
    @Parameters({
        @Parameter(name = "userId", description = "유저의 아이디, header에 담아주시면 됩니다.")
    })
    public SuccessResponse<myPageDTO> getMyPage(@RequestHeader(name = "userId") Long userId) {
        //소셜로그인이 들어가면 위 헤더 부분이 사라지고 토큰으로 유저를 구분해야함.

        User user = userService.getUser(userId);

        return new SuccessResponse<>(MyPageConverter.toMyPageDTO(user));
    }



}
