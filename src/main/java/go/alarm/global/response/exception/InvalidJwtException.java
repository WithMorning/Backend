package go.alarm.global.response.exception;

import go.alarm.global.response.ResponseCode;
import lombok.Getter;

@Getter
public class InvalidJwtException extends AuthException {

    public InvalidJwtException(final ResponseCode responseCode) {
        super(responseCode);
    }
}