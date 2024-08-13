package go.alarm.service;

import go.alarm.domain.entity.User;
import go.alarm.web.dto.request.UserRequestDTO;

public interface UserService {
    User getUser(Long userId);

    User setBedTime(Long userId, UserRequestDTO.SetBedTimeDTO request);

}
