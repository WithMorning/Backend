package go.alarm.web.controller;

import go.alarm.domain.entity.User;
import go.alarm.response.BaseResponse;
import go.alarm.service.AlarmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/")
@Slf4j
public class TestController {

    private final AlarmService alarmService;

    @GetMapping("/health")
    @Operation(summary = "헬스 체크 API", description = "서버 상태를 테스트합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "요청에 성공하였습니다.")
    })
    public BaseResponse<String> healthCheck() {

        return new BaseResponse<>("Health Check");
    }

    @PostMapping("/alarm/test")
    @Operation(summary = "알람 테스트 API", description = "FCM 알람을 테스트합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "요청에 성공하였습니다.")
    })
    public BaseResponse<List<String>> pushAlarmTest() {
        ArrayList<String> userNameList = new ArrayList<>();
        List<User> userList = alarmService.sendAlarmsTest();

        for (User user : userList){
            userNameList.add(user.getNickname());
        }

        return new BaseResponse<>(userNameList);
    }

}
