package go.alarm.service;

import go.alarm.domain.entity.Group;
import go.alarm.domain.entity.User;
import go.alarm.domain.entity.UserGroup;
import go.alarm.domain.repository.GroupRepository;
import go.alarm.domain.repository.UserGroupRepository;
import go.alarm.domain.repository.UserRepository;
import go.alarm.web.converter.MainConverter;
import go.alarm.web.dto.MainResponseDTO;
import go.alarm.web.dto.MainResponseDTO.GroupDTO;
import go.alarm.web.dto.MainResponseDTO.MainDTO;
import go.alarm.web.dto.MainResponseDTO.UserDTO;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserGroupServiceImpl implements UserGroupService{

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;

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

    @Override
    public List<UserDTO> getUserDTOListByGroup(Group group) {

        List<UserGroup> userGroups = userGroupRepository.findAllByGroup(group);

        return userGroups.stream()
            .map(ug -> MainConverter.userDTO(ug.getUser(), ug))
            .collect(Collectors.toList());
    }

    @Override
    public GroupDTO getGroupDTOWithUsers(Group group) {

        List<MainResponseDTO.UserDTO> userDTOList = getUserDTOListByGroup(group);
        return MainConverter.groupDTO(group, userDTOList);
    }

    @Override
    public MainDTO getMainDTO(Long userId) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        String connectorProfileURL = user.getImageURL();
        List<Group> groupList = getGroupListByUserId(userId);
        List<MainResponseDTO.GroupDTO> groupDTOList = groupList.stream()
            .map(this::getGroupDTOWithUsers)
            .collect(Collectors.toList());

        return MainConverter.mainDTO(connectorProfileURL, groupDTOList);
    }


}
