package go.alarm.user.service;

import go.alarm.user.domain.User;
import go.alarm.user.dto.UserRequestDTO;

public interface UserService {
    User getUser(Long userId);

    User setBedTime(Long userId, UserRequestDTO.SetBedTimeDTO request);

}
