package go.alarm.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DisturbBanModeRequest {

    @NotNull(message = "방해금지모드 설정 여부를 입력해주세요. (true/false)")
    private Boolean isDisturbBanMode;

}
