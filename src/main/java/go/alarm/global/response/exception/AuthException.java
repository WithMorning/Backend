package go.alarm.global.response.exception;

import go.alarm.global.response.ResponseCode;
import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {

    private final int code;
    private final String message;

    public AuthException(ResponseCode responseCode) {
        super(responseCode.getMessage());  // 부모 클래스에 메시지 전달
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    // cause를 받는 생성자 추가
    public AuthException(ResponseCode responseCode, Throwable cause) {
        super(responseCode.getMessage(), cause);  // 부모 클래스에 메시지와 cause 전달
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

}