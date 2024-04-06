package go.alarm.service;

import go.alarm.domain.entity.Group;
import go.alarm.web.dto.GroupRequestDTO;

public interface GroupCommandService {

    Group createGroup(Long userId, GroupRequestDTO.CreateDTO request);

    Group createMemo(Long userId, Long storeId, GroupRequestDTO.CreateMemoDTO request);
}
