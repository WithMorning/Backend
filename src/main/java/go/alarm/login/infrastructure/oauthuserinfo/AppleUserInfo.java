package go.alarm.login.infrastructure.oauthuserinfo;

import static lombok.AccessLevel.PRIVATE;
import com.fasterxml.jackson.annotation.JsonProperty;
import go.alarm.login.domain.OauthUserInfo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor
public class AppleUserInfo implements OauthUserInfo {

    @JsonProperty("sub")
    private String socialLoginId; // 애플에서 제공하는 고유한 사용자 ID,  JWT의 'sub' 클레임에서 제공됨

    @JsonProperty("email")
    private String email; // 애플에서는 이메일을 제공, 사용자가 거부하면 애플에서 생성한 임의의 이메일이 됨.

    @Override
    public String getSocialLoginId() {
        return socialLoginId;
    }

    @Override
    public String getEmail() {
        return email;
    }
}