package go.alarm.web.controller;

import go.alarm.domain.entity.Group;
import go.alarm.response.BaseResponse;
import go.alarm.web.converter.MainConverter;
import go.alarm.web.dto.MainResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
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
@RequestMapping("/main")
@Slf4j
public class MainController {

    @GetMapping
    @Operation(summary = "메인페이지 조회 API", description = "메인페이지를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "OK, 성공입니다.")
    })
    public BaseResponse<MainResponseDTO> getMainPage(@RequestHeader(name = "userId") Long userId) {
        //소셜로그인이 들어가면 위 헤더 부분이 사라지고 토큰으로 유저를 구분해야함.

       // List<Group> mainPage = mainQueryService.getMain(userId);

       // return new BaseResponse<>(MainConverter.MainDTO(mainPage));
        return null;
    }
}
