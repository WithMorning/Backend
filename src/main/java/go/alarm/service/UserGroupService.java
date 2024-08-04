package go.alarm.service;

import go.alarm.domain.entity.Group;
import go.alarm.web.dto.response.MainResponseDTO;
import java.util.List;

public interface UserGroupService {

    List<Group> getGroupListByUserId(Long userId);
    List<MainResponseDTO.UserDTO> getUserDTOListByGroup(Group group);
    MainResponseDTO.GroupDTO getGroupDTOWithUsers(Group group);
    MainResponseDTO.MainDTO getMainDTO(Long userId);


}
