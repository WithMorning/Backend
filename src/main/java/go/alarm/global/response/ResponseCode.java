package go.alarm.global.response;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum ResponseCode {
    
    /**
     * 200번 >> 요청 성공
     * 500번 >> 서버 에러
     * 1000번대 >> 그룹 관련 예외
     * 2000번대 >> 알람 관련 예외
     * 3000번대 >>
     * 5000번대 >> 이미지 관련 예외
     * 8000번대 >> 유저 관련 예외
     * 9000번대 >> 로그인 관련 예외
     *
     * */
    SUCCESS(true, 200, "요청에 성공하였습니다."),
    INTERNAL_SEVER_ERROR(false, 500, "서버 에러가 발생하였습니다. 관리자에게 문의해 주세요."),

    INVALID_REQUEST(false, 1000, "올바르지 않은 요청입니다."),

    NOT_FOUND_GROUP_ID(false, 1001, "요청한 ID에 해당하는 그룹이 존재하지 않습니다."),
    NOT_FOUND_JOIN_CODE(false, 1002, "요청한 참여 코드의 그룹이 존재하지 않습니다."),
    DUPLICATED_JOIN_USER(false, 1003, "해당 유저는 해당 그룹에 이미 참여했습니다."),
    EXCEED_USER_SIZE(false, 1004, "참여하려는 그룹의 인원수가 다 찼습니다."),


    EXCEED_IMAGE_CAPACITY(false, 5001, "업로드 가능한 이미지 용량을 초과했습니다."),
    NULL_IMAGE(false, 5002, "업로드한 이미지 파일이 NULL입니다."),
    EMPTY_IMAGE_LIST(false, 5003, "최소 한 장 이상의 이미지를 업로드해야합니다."),
    EXCEED_IMAGE_LIST_SIZE(false, 5004, "업로드 가능한 이미지 개수를 초과했습니다."),
    INVALID_IMAGE_URL(false, 5005, "요청한 이미지 URL의 형식이 잘못되었습니다."),
    INVALID_IMAGE_PATH(false, 5101, "이미지를 저장할 경로가 올바르지 않습니다."),
    INVALID_IMAGE(false, 5102, "올바르지 않은 이미지 파일입니다."),


    INVALID_USER(false, 8001, "존재하지 않는 유저입니다."),
    NOT_GROUP_HOST(false, 8002, "해당 유저는 방장이 아닙니다."),
    DUPLICATED_ADMIN_USERNAME(false, 8003, "중복된 사용자 닉네임입니다."),
    NOT_FOUND_USER_ID(false, 8004, "요청한 ID에 해당하는 유저가 존재하지 않습니다."),
    INVALID_CURRENT_PASSWORD(false, 8005, "현재 사용중인 비밀번호가 일치하지 않습니다."),
    FAIL_SEND_SMS(false, 8006, "휴대폰 본인 인증 SMS 발송을 실패했습니다."),
    ERROR_SEND_SMS(false, 8007, "휴대폰 본인 인증 SMS 발송 중 오류가 발생했습니다."),
    UNMATCHED_CODE(false, 8008, "인증번호가 일치하지 않습니다."),

    NULL_ADMIN_AUTHORITY(false, 8101, "잘못된 관리자 권한입니다."),
    NOT_FOUND_ADMIN_ID(false, 8202, "요청한 ID에 해당하는 관리자를 찾을 수 없습니다."),
    INVALID_ADMIN_AUTHORITY(false, 8202, "해당 관리자 기능에 대한 접근 권한이 없습니다."),


    INVALID_AUTHORIZATION_CODE(false, 9001, "유효하지 않은 인증 코드입니다."),
    NOT_SUPPORTED_OAUTH_SERVICE(false, 9002, "해당 OAuth 서비스는 제공하지 않습니다."),
    FAIL_TO_CONVERT_URL_PARAMETER(false, 9003, "Url Parameter 변환 중 오류가 발생했습니다."),
    INVALID_REFRESH_TOKEN(false, 9101, "올바르지 않은 형식의 RefreshToken입니다."),
    INVALID_ACCESS_TOKEN(false, 9102, "올바르지 않은 형식의 AccessToken입니다."),
    EXPIRED_PERIOD_REFRESH_TOKEN(false, 9103, "기한이 만료된 RefreshToken입니다."),
    EXPIRED_PERIOD_ACCESS_TOKEN(false, 9104, "기한이 만료된 AccessToken입니다."),
    FAIL_TO_VALIDATE_TOKEN(false, 9105, "토큰 유효성 검사 중 오류가 발생했습니다."),
    NOT_FOUND_REFRESH_TOKEN(false, 9106, "RefreshToken이 null이거나 빈 문자열입니다."),
    FAIL_REVOKE_APPLE_TOKEN(false, 9106, "애플 토큰 삭제를 실패했습니다."),
    FAIL_GET_APPLE_TOKEN(false, 9106, "애플 리프레시 토큰 발급을 실패했습니다."),
    FAIL_CREATE_CLIENT_SECRET(false, 9106, "Apple Client Secret을 생성하던 도중에 에러가 발생했습니다."),
    NOT_FOUND_KEY_FILE(false, 9107, "프라이빗 키 파일을 찾을 수 없습니다."),
    NOT_FOUND_REQUIRED_PARAM(false, 9108, "필수 파라미터가 없습니다."),
    NOT_FOUND_AUTHORIZATION_CODE(false, 9108, "Authorization code가 없거나 null입니다."),
    NOT_FOUND_CLIENT_SECRET(false, 9109, "생성된 client secret이 없거나 null입니다."),
    NOT_FOUND_REDIRECT_URI(false, 9110, "redirect uri가 없거나 null입니다."),
    INVALID_AUTHORITY(false, 9201, "해당 요청에 대한 접근 권한이 없습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    ResponseCode(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
