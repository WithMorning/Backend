package go.alarm.user.presentation;

import go.alarm.user.domain.User;
import go.alarm.user.dto.UserResponseDTO;

public class UserConverter {

    public static UserResponseDTO.setUserBedTimeDTO setBedTime(User user){
        return UserResponseDTO.setUserBedTimeDTO.builder()
            .userId(user.getId())
            .build();
    }
}
