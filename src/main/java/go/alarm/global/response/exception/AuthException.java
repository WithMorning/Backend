package go.alarm.global.response.exception;

import go.alarm.global.response.ResponseCode;
import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {

    private final int code;
    private final String message;

    public AuthException(ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }
}