package go.alarm.web.converter;

import go.alarm.domain.entity.Group;
import go.alarm.domain.entity.User;
import go.alarm.web.dto.GroupRequestDTO.CreateGroupDTO;
import go.alarm.web.dto.GroupResponseDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class GroupConverter {

    public static GroupResponseDTO.CreateGroupDto toCreateGroupDTO(Group group){
        return GroupResponseDTO.CreateGroupDto.builder()
            .groupId(group.getId())
            .createdAt(LocalDateTime.now())
            .build();
    }

    public static Group toGroup(CreateGroupDTO request){

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

    public static GroupResponseDTO.UpdateGroupDto toUpdateGroupDTO(Group group){
        return GroupResponseDTO.UpdateGroupDto.builder()
            .groupId(group.getId())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    public static GroupResponseDTO.DeleteGroupDto toDeleteGroupDTO(){
        return GroupResponseDTO.DeleteGroupDto.builder()
            .deletedAt(LocalDateTime.now())
            .build();
    }

    public static GroupResponseDTO.JoinGroupDto toJoinGroupDTO(Group group, User user){
        return GroupResponseDTO.JoinGroupDto.builder()
            .groupId(group.getId())
            .joinUserNickname(user.getNickname())
            .createdAt(LocalDateTime.now())
            .build();
    }
}
