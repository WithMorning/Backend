package go.alarm.global.response.exception;

import go.alarm.global.response.ResponseCode;
import lombok.Getter;

@Getter
public class RefreshTokenException extends AuthException {

    public RefreshTokenException(final ResponseCode responseCode) {
        super(responseCode);
    }
}