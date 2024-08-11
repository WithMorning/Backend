package go.alarm.login.domain;

/*
 * 소셜 로그인을 통해 Resource Server로부터 받아온 유저의 정보를 조회하는 인터페이스
 * 추후 다양한 Resource Server를 추가하기 위해 인터페이스로 구현 (현재는 AppleUserInfo만 존재합니다.)
 * */
public interface OauthUserInfo {
    String getSocialLoginId();

    String getEmail();

}