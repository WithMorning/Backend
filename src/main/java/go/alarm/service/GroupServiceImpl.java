package go.alarm.service;

import go.alarm.domain.entity.Group;
import go.alarm.domain.entity.UserGroup;
import go.alarm.domain.entity.WakeupDate;
import go.alarm.domain.repository.GroupRepository;
import go.alarm.domain.repository.UserRepository;
import go.alarm.web.converter.GroupConverter;
import go.alarm.web.converter.UserGroupConverter;
import go.alarm.web.converter.WakeupDateConverter;
import go.alarm.web.dto.GroupRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional//@Transactional(readOnly = true) 여기 readOnly=true가 되니까 권한이 읽기 권한만 존재함.
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Override
    public Group createGroup(Long userId, GroupRequestDTO.CreateDTO request) {

        Group group = GroupConverter.toGroup(request); // 그룹 생성
        WakeupDate wakeupDate = WakeupDateConverter.toWakeupDate(group, request.getWakeupDateList()); // 기상 요일 설정
        group.setWakeupDate(wakeupDate);

        UserGroup userGroup = UserGroupConverter.toUserGroup(userRepository.findById(userId).get(), request.getIsAgree()); // 유저 그룹 생성
        System.out.println("group = " + group.getUserGroupList());
        userGroup.setGroup(group); // userGroup을 userGroupRepository.save로 저장해야 하나..?
        // 아니다. setGroup을 들어가보면 group.~~.add로 userGroup객체를 넣어준다.


        // 여기다가 user - userGroup 매핑을 해줘야 함...? 로직이 너무 거대해지는데..? 일단 이건 keep..

        // 여기에 UserGroup도 같이 생성해주는 로직이 추가되어야 함.
        // 이후 인원 초대를 할 경우 UserGroup 테이블에 new 유저 데이터를 추가
        // 만약 비회원이 초대코드를 받았다면 회원가입(User 테이블에 삽입) + UserGroup에도 함께 삽입
        
        return groupRepository.save(group);
    }

    @Override
    public Group createMemo(Long userId, Long groupId, GroupRequestDTO.CreateMemoDTO request) {

        // userId를 통해 해당 유저가 이 그룹에 속해있는지 예외 처리를 이 서비스 단에서 하냐? 컨트롤러에서 하냐?
        // 아니면 umc 워크북처럼 따로 예외처리 어노테이션을 만들어서 컨트롤러에서 하냐?는 나중에 정해야 할듯.
        Group group = groupRepository.findById(groupId).get();
        group.setMemo(request.getMemo());
        System.out.println(request.getMemo());
        return group;
    }

    @Override
    public UserGroup joinGroup(Long userId, GroupRequestDTO.JoinGroupDTO request) {
        Group group = groupRepository.findByParticipationCode(request.getParticipationCode()); // 참여 코드로 그룹 찾기
        // 참여 코드가 없을 시 예외 처리도 해줘야 함.
        // 유저가 존재하는 지 예외 처리 해줘야 함.

        UserGroup userGroup = UserGroupConverter.toUserGroup(userRepository.findById(userId).get(),request.getIsAgree()); // 유저 그룹 생성(그룹에 유저 넣기)
        userGroup.setGroup(group);
        // 그룹에 인원수가 다 찼다면 참여가 불가능한 예외 처리도 해줘야 함.

        return userGroup;
    }
}
