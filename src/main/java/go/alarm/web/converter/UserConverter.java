package go.alarm.web.converter;

import go.alarm.domain.entity.User;
import go.alarm.web.dto.UserResponseDTO;

public class UserConverter {

    public static UserResponseDTO.setUserBedTimeDTO setBedTime(User user){
        return UserResponseDTO.setUserBedTimeDTO.builder()
            .userId(user.getId())
            .build();
    }
}
