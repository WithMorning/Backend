package go.alarm.web.converter;


import go.alarm.domain.entity.User;
import go.alarm.domain.entity.UserGroup;

public class UserGroupConverter {

    public static UserGroup toUserGroup(User user, Boolean isAgree){

        return UserGroup.builder()
            .user(user)
            .phone(user.getPhone())
            .isAgree(isAgree)
            .isWakeup(Boolean.FALSE)
            .isDisturbBanMode(Boolean.FALSE)
            .build();
    }

}
