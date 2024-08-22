package go.alarm.global.response.exception;

import static go.alarm.global.response.ResponseCode.EXCEED_IMAGE_CAPACITY;
import static go.alarm.global.response.ResponseCode.INTERNAL_SEVER_ERROR;
import static go.alarm.global.response.ResponseCode.INVALID_REQUEST;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 전역 예외 처리기입니다.
 * body로 클라이언트에게 유용한 피드백을 제공하면서, 로깅을 추가하여 서버 측에서도 문제를 효과적으로 추적할 수 있게 해줍니다.
 * */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /*
     * @Valid 어노테이션으로 검증 실패 시 발생하는 예외를 처리합니다.
     * */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException e, //발생한 예외 객체
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        // 예외 메시지를 WARN 레벨로 로깅

        String errMessage = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        // BindingResult에서 첫 번째 FieldError의 DefaultMessage를 가져옵니다. 이후 FieldError가 null이 아님을 보장합니다.

        return ResponseEntity.badRequest() // HTTP 상태 코드 400으로 응답을 시작
            .body(new ExceptionResponse(INVALID_REQUEST.isSuccess(), INVALID_REQUEST.getCode(), errMessage)); // 응답 body 생성
    }

    /*
     * 파일 업로드 크기 제한 초과 시 발생하는 예외를 처리합니다.
     * */
    @ExceptionHandler(SizeLimitExceededException.class)
    public ResponseEntity<ExceptionResponse> handleSizeLimitExceededException(SizeLimitExceededException e) {
        log.warn(e.getMessage(), e);

        String message = EXCEED_IMAGE_CAPACITY.getMessage()
            + " 입력된 이미지 용량은 " + e.getActualSize() + " byte 입니다. "
            + "(제한 용량: " + e.getPermittedSize() + " byte)";
        return ResponseEntity.badRequest()
            .body(new ExceptionResponse(EXCEED_IMAGE_CAPACITY.isSuccess(), EXCEED_IMAGE_CAPACITY.getCode(), message));
    }

    /*
     * 로그인 관련 예외를 처리합니다.
     * */
    @ExceptionHandler(AuthException.class) //AuthException 클래스의 예외가 발생했을 때 이 메소드가 처리함을 명시
    public ResponseEntity<ExceptionResponse> handleAuthException(AuthException e) {
        log.warn(e.getMessage(), e);

        return ResponseEntity.badRequest()
            .body(new ExceptionResponse(false, e.getCode(), e.getMessage()));
    }

    /*
     * 관리자 관련 예외를 처리합니다.
     * */
    @ExceptionHandler(AdminException.class)
    public ResponseEntity<ExceptionResponse> handleAdminException(AdminException e) {
        log.warn(e.getMessage(), e);

        return ResponseEntity.badRequest()
            .body(new ExceptionResponse(false, e.getCode(), e.getMessage()));
    }

    /*
     * 일반적인 잘못된 요청 예외를 처리합니다.
     * */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException e) {
        log.warn(e.getMessage(), e);

        return ResponseEntity.badRequest()
            .body(new ExceptionResponse(false, e.getCode(), e.getMessage()));
    }

    /*
     * 위의 메소드들로 처리되지 않은 모든 예외를 캐치합니다.
     * */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        log.error(e.getMessage(), e);

        return ResponseEntity.internalServerError()
            .body(new ExceptionResponse(INTERNAL_SEVER_ERROR.isSuccess(), INTERNAL_SEVER_ERROR.getCode(), INTERNAL_SEVER_ERROR.getMessage()));
    }
}