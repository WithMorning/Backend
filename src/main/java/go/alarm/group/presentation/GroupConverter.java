package go.alarm.group.presentation;

import go.alarm.group.domain.Group;
import go.alarm.group.domain.UserGroup;
import go.alarm.group.dto.request.GroupRequest;
import go.alarm.group.dto.response.GroupJoinResponse;
import go.alarm.user.domain.User;
import go.alarm.group.dto.response.GroupResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * 엔티티를 생성하거나 응답 형식에 맞게 변환하고자 Converter를 사용합니다
 * */
public class GroupConverter {

    /**
     * 알람 그룹 생성 후 그룹 Id, 생성 날짜 및 시간을 반환해줍니다.
     * */
    public static GroupResponse createGroup(Group group){
        return GroupResponse.builder()
            .groupId(group.getId())
            .createdAt(LocalDateTime.now())
            .build();
    }

    /**
     * 알람 그룹 엔티티를 생성하여 반환해줍니다.
     * */
    public static Group toGroup(GroupRequest request){

        int codeLength = 8;

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";

        StringBuilder sb = new StringBuilder(codeLength);

        for (int i = 0; i < codeLength; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }

        return Group.builder()
            .name(request.getName()) // 여기서 get~~은 DTO에 있는 친구들만 가져올 수 있음.
            .wakeupTime(request.getWakeupTime())
            //.dayOfWeek(dayOfWeek.builder() 이런 식으로 한 번에 dayOfWeek 설정해줄 수 있으나,
            //    .mon(true)                    나는 가독성을 위해 dayOfWeekConverter를 새로 만듦.
            //    .build())                     마찬가지로 UserGroup도 여기서 생성해줄 수 있으나, 따로 뺌.
            .userGroupList(new ArrayList<>())
            // Group을 생성할 때 이 유저 리스트를 생성하는 부분을 생략했었음. 이후 userGroup의 setGroup에서
            // group.getUserGroupList()의 반환값이 null이 되는 것. 여기에 .add를 해버리니까 (null에 add를 하니까)
            // userGroupList에 NullPointerException이 일어남
            .participationCode(sb.toString())
            .memo(request.getMemo())
            .build();
    }

    /**
     * 알람 그룹에 참여했을 때 참여한 알람 그룹 Id와 유저 닉네임을 반환해줍니다.
     * */
    public static GroupJoinResponse joinGroup(Group group, User user){
        return GroupJoinResponse.builder()
            .groupId(group.getId())
            .joinUserNickname(user.getNickname())
            .joinAt(LocalDateTime.now())
            .build();
    }

    /**
     * 유저-알람그룹 엔티티를 생성하여 반환해줍니다.
     * */
    public static UserGroup toUserGroup(User user, Boolean isAgree){

        return UserGroup.builder()
            .user(user)
            .phone(user.getPhone())
            .isAgree(isAgree)
            .isWakeup(Boolean.TRUE)
            .isDisturbBanMode(Boolean.FALSE)
            .build();
    }
}
