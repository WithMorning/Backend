package go.alarm.service;

import go.alarm.domain.entity.Group;
import go.alarm.domain.entity.UserGroup;
import go.alarm.web.dto.GroupRequestDTO;

public interface GroupService {

    Group createGroup(Long userId, GroupRequestDTO.CreateDTO request);

    UserGroup joinGroup(Long userId, GroupRequestDTO.JoinGroupDTO request);


}
