package go.alarm.user.service;

import go.alarm.user.domain.User;
import go.alarm.user.dto.request.UserBedTimeRequest;
import go.alarm.user.dto.request.UserProfileRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    User getUser(Long userId);

    User setBedTime(Long userId, UserBedTimeRequest request);

    void setProfile(Long userId, UserProfileRequest request, MultipartFile iamge);

    void sendVerificationCode(String phone);

    void verifyCode(String phone, String code, Long userId);

    void setDisturbBanMode(Long userId, Long groupId, Boolean isDisturbBanMode);

    void setWakeStatus(Long userId, Long groupId);

    void setPhoneAgree(Long userId, Long groupId, Boolean isAgree);

    void setSleepStatus();

}
