package go.alarm.home.service;

import go.alarm.group.domain.Group;
import go.alarm.home.dto.response.HomeResponse;
import go.alarm.user.domain.User;
import java.util.List;

public interface HomeService {

    List<Group> getGroupListByUserId(Long userId);
    List<HomeResponse.UserDTO> getUserDTOListByGroup(Group group);
    HomeResponse.GroupDTO getGroupDTOWithUsers(Group group, User user);
    HomeResponse.HomeDTO getHome(Long userId);

}
