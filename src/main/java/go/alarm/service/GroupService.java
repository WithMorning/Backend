package go.alarm.service;

import go.alarm.domain.entity.Group;
import go.alarm.domain.entity.UserGroup;
import go.alarm.web.dto.GroupRequestDTO;

public interface GroupService {

    Group createGroup(Long userId, GroupRequestDTO.CreateGroupDTO request);

    Group updateGroup(Long userId, Long groupId, GroupRequestDTO.UpdateGroupDTO request);

    void deleteGroup(Long userId, Long groupId);

    UserGroup joinGroup(Long userId, GroupRequestDTO.JoinGroupDTO request);

}
