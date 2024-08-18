package go.alarm.home.service;

import go.alarm.group.domain.Group;
import go.alarm.home.dto.HomeResponseDTO;
import java.util.List;

public interface HomeService {

    List<Group> getGroupListByUserId(Long userId);
    List<HomeResponseDTO.UserDTO> getUserDTOListByGroup(Group group);
    HomeResponseDTO.GroupDTO getGroupDTOWithUsers(Group group);
    HomeResponseDTO.HomeDTO getMainDTO(Long userId);


}
