package go.alarm.group.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class GroupJoinRequest {

    @NotBlank(message = "알람 그룹의 참여 코드를 입력해주세요.")
    private String participationCode;

    @NotNull(message = "알람 그룹의 알람 수신 동의 여부를 입력해주세요.")
    private Boolean isAgree;
}
