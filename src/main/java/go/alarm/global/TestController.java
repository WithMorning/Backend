package go.alarm.global;

import go.alarm.user.domain.User;
import go.alarm.global.response.SuccessResponse;
import go.alarm.fcm.service.FCMService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/")
@Slf4j
public class TestController {

    private final FCMService FCMService;

    @GetMapping("/health")
    @Operation(summary = "헬스 체크 API", description = "서버 상태를 테스트합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "요청에 성공하였습니다.")
    })
    public SuccessResponse<String> healthCheck() {

        return new SuccessResponse<>("Health Check");
    }

    @GetMapping("/alarm/test")
    @Operation(summary = "알람 테스트 API", description = "FCM 알람을 테스트합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "요청에 성공하였습니다.")
    })
    public SuccessResponse<List<String>> pushAlarmTest() {
        ArrayList<String> userNameList = new ArrayList<>();
        List<User> userList = FCMService.sendAlarmsTest();

        for (User user : userList){
            userNameList.add(user.getNickname());
        }

        return new SuccessResponse<>(userNameList);
    }

}
