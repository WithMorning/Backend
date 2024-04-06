package go.alarm.web.converter;

import go.alarm.domain.entity.Group;
import go.alarm.domain.entity.WakeupDate;
import go.alarm.web.dto.GroupRequestDTO;
import go.alarm.web.dto.GroupRequestDTO.CreateDTO;
import go.alarm.web.dto.GroupResponseDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GroupConverter {

    public static GroupResponseDTO.CreateResultDto toCreateResultDTO(Group Group){
        return GroupResponseDTO.CreateResultDto.builder()
            .groupId(Group.getId())
            .createdAt(LocalDateTime.now())
            .build();
    }

    public static Group toGroup(CreateDTO request){
        return Group.builder()
            .name(request.getName()) // 여기서 get~~은 DTO에 있는 친구들만 가져올 수 있음.
            .wakeupTime(request.getWakeupTime())
            //.wakeupDate(WakeupDate.builder() 이런 식으로 한 번에 wakeupDate를 설정해줄 수 있으나,
            //    .mon(true)                    나는 가독성을 위해 wakeupDateConverter를 새로 만듦.
            //    .build())                     마찬가지로 UserGroup도 여기서 생성해줄 수 있으나, 따로 뺌.
            .userGroupList(new ArrayList<>())
            // Group을 생성할 때 이 유저 리스트를 생성하는 부분을 생략했었음. 이후 userGroup의 setGroup에서
            // group.getUserGroupList()의 반환값이 null이 되는 것. 여기에 .add를 해버리니까 (null에 add를 하니까)
            // userGroupList에 NullPointerException이 일어남
            .build();
    }
}
