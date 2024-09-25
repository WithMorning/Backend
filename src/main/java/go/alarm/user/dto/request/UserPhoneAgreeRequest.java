package go.alarm.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserPhoneAgreeRequest {

    @NotNull(message = "유저가 설정한 전화번호 공개 여부를 입력해주세요.")
    private Boolean isAgree;

}
