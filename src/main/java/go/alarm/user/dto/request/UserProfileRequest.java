package go.alarm.user.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class UserProfileRequest {

    @NotNull(message = "유저의 닉네임을 입력해주세요.")
    private String  nickname;

    @NotNull(message = "유저의 FCM 토큰을 입력해주세요.")
    private String fcmToken;

    private MultipartFile image;
}
