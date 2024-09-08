package go.alarm.global.response.exception;

import go.alarm.global.response.ResponseCode;
import lombok.Getter;

@Getter
public class VerifyCodeException extends RuntimeException {

    private final int code;
    private final String message;

    public VerifyCodeException(final ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }
}
