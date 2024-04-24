package go.alarm.service;

import go.alarm.domain.entity.Group;
import go.alarm.domain.entity.UserGroup;
import go.alarm.web.dto.GroupRequestDTO;

public interface GroupService {

    Group createGroup(Long userId, GroupRequestDTO.CreateDTO request);

    Group createMemo(Long userId, Long storeId, GroupRequestDTO.CreateMemoDTO request);

    UserGroup joinGroup(Long userId, GroupRequestDTO.JoinGroupDTO request);

}
