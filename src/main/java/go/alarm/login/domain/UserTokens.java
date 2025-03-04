package go.alarm.login.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
* 유저의 토큰을 저장하는 객체
* */
@RequiredArgsConstructor
@Getter
public class UserTokens {

    private final String refreshToken;
    private final String accessToken;
    private Boolean isNewUser;

    public Boolean getIsNewUser() {
        return isNewUser;
    }

    public void setIsNewUser(Boolean isNewUser) {
        this.isNewUser = isNewUser;
    }
}