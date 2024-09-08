package go.alarm.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PhoneSmsRequest {

    @NotNull(message = "유저의 전화번호를 입력해주세요.")
    private String phone;

}
