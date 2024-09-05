package go.alarm.user.service;

import go.alarm.user.domain.User;
import go.alarm.user.dto.request.UserBedTimeRequest;
import go.alarm.user.dto.request.UserProfileRequest;

public interface UserService {
    User getUser(Long userId);

    User setBedTime(Long userId, UserBedTimeRequest request);

    void setProfile(Long userId, UserProfileRequest request);

}
