package go.alarm.home.presentation;

import go.alarm.auth.Auth;
import go.alarm.auth.UesrOnly;
import go.alarm.auth.domain.Accessor;
import go.alarm.global.response.SuccessResponse;
import go.alarm.home.dto.response.HomeResponse;
import go.alarm.home.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping
@Slf4j
public class HomeController {

        private final HomeService homeService;

        @GetMapping("/home")
        @UesrOnly
        @Operation(summary = "메인페이지 조회 API", description = "메인페이지를 조회합니다.")
        @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "OK, 성공입니다.")
        })
        public SuccessResponse<HomeResponse.HomeDTO> getMainPage(
            @Auth final Accessor accessor){

                return new SuccessResponse<>(homeService.getHome(accessor.getUserId()));
        }
}
