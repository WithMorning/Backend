package go.alarm.home.service;

import go.alarm.group.domain.Group;
import go.alarm.user.domain.User;
import go.alarm.group.domain.UserGroup;
import go.alarm.group.domain.repository.UserGroupRepository;
import go.alarm.user.domain.repository.UserRepository;
import go.alarm.home.presentation.HomeConverter;
import go.alarm.home.dto.response.HomeResponse;
import go.alarm.home.dto.response.HomeResponse.GroupDTO;
import go.alarm.home.dto.response.HomeResponse.HomeDTO;
import go.alarm.home.dto.response.HomeResponse.UserDTO;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class HomeServiceImpl implements HomeService {

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;

    /**
     * 메인 페이지 정보를 반환합니다.
     * */
    @Override
    public HomeDTO getMainDTO(Long userId) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        String connectorProfileURL = user.getImageURL();
        List<Group> groupList = getGroupListByUserId(userId);
        List<HomeResponse.GroupDTO> groupDTOList = groupList.stream()
            .map(this::getGroupDTOWithUsers)
            .collect(Collectors.toList());

        return HomeConverter.homeDTO(connectorProfileURL, groupDTOList);
    }

    /**
     * 유저 Id로 유저가 속한 그룹 리스트를 조회합니다.
     * */
    @Override
    public List<Group> getGroupListByUserId(Long userId) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다.")); 
        // 모각 깃허브 보면 .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_JOB)); 이런 식으로 예외처리함

        List<UserGroup> userGroupList = userGroupRepository.findAllByUser(user);
        return userGroupList.stream()
            .map(UserGroup::getGroup)
            .collect(Collectors.toList());
    }

    /**
     * 그룹과 그룹에 속한 유저 리스트를 반환합니다.
     * */
    @Override
    public GroupDTO getGroupDTOWithUsers(Group group) {

        List<UserDTO> userDTOList = getUserDTOListByGroup(group);
        return HomeConverter.groupDTO(group, userDTOList);
    }


    /**
     * 그룹 객체로 유저 정보 리스트를 조회합니다.
     * */
    @Override
    public List<UserDTO> getUserDTOListByGroup(Group group) {

        List<UserGroup> userGroups = userGroupRepository.findAllByGroup(group);

        return userGroups.stream()
            .map(ug -> HomeConverter.userDTO(ug.getUser(), ug))
            .collect(Collectors.toList());
    }
}
