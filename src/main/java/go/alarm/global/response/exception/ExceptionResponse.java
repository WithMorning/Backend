package go.alarm.global.response.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExceptionResponse {

    private final Boolean isSuccess;
    private final int code;
    private final String message;
}