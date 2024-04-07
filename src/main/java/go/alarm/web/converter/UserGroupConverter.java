package go.alarm.web.converter;


import go.alarm.domain.entity.User;
import go.alarm.domain.entity.UserGroup;

public class UserGroupConverter {

    public static UserGroup toUserGroup(User user){

        return UserGroup.builder()
            .user(user)
            .phone(user.getPhone())
            .build();
    }

}
