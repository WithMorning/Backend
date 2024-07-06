package go.alarm.service;

import go.alarm.domain.entity.Group;
import go.alarm.domain.entity.UserGroup;
import go.alarm.domain.entity.WakeupDate;
import go.alarm.domain.repository.GroupRepository;
import go.alarm.domain.repository.UserGroupRepository;
import go.alarm.domain.repository.UserRepository;
import go.alarm.web.converter.GroupConverter;
import go.alarm.web.converter.UserGroupConverter;
import go.alarm.web.converter.WakeupDateConverter;
import go.alarm.web.dto.GroupRequestDTO;
import go.alarm.web.dto.GroupRequestDTO.UpdateGroupDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional//@Transactional(readOnly = true) 여기 readOnly=true가 되니까 권한이 읽기 권한만 존재함.
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;

    @Override
    public Group createGroup(Long userId, GroupRequestDTO.CreateGroupDTO request) {

        Group group = GroupConverter.toGroup(request); // 그룹 생성
        WakeupDate wakeupDate = WakeupDateConverter.toWakeupDate(group, request.getWakeupDateList()); // 기상 요일 설정
        group.setWakeupDate(wakeupDate);

        UserGroup userGroup = UserGroupConverter.toUserGroup(userRepository.findById(userId).get(), request.getIsAgree()); // 유저 그룹 생성
        userGroup.setGroup(group); // userGroup을 userGroupRepository.save로 저장해야 하나..?
        // 아니다. setGroup을 들어가보면 group.~~.add로 userGroup객체를 넣어준다.


        // 여기다가 user - userGroup 매핑을 해줘야 함...? 로직이 너무 거대해지는데..? 일단 이건 keep..

        // 여기에 UserGroup도 같이 생성해주는 로직이 추가되어야 함.
        // 이후 인원 초대를 할 경우 UserGroup 테이블에 new 유저 데이터를 추가
        // 만약 비회원이 초대코드를 받았다면 회원가입(User 테이블에 삽입) + UserGroup에도 함께 삽입
        
        return groupRepository.save(group);
    }

    @Override
    public Group updateGroup(Long userId, Long groupId, UpdateGroupDTO request) {

        Group group = groupRepository.findById(groupId).get();

        group.setWakeupTime(request.getWakeupTime());
        updateWakeupDate(group.getWakeupDate(), request.getWakeupDateList());
        group.setName(request.getName());
        group.setMemo(request.getMemo());

        return group;
    }

    public WakeupDate updateWakeupDate(WakeupDate wakeupDate, List<String> wakeupDateList){
        
        wakeupDate.resetWakeupDate(); // 기존 기상 요일 리셋

        for (String dayOfWeek : wakeupDateList) {
            if (dayOfWeek.equals("mon")) {
                wakeupDate.setMon(Boolean.TRUE);
            } else if (dayOfWeek.equals("tue")) {
                wakeupDate.setTue(Boolean.TRUE);
            } else if (dayOfWeek.equals("wed")) {
                wakeupDate.setWed(Boolean.TRUE);
            } else if (dayOfWeek.equals("thu")) {
                wakeupDate.setThu(Boolean.TRUE);
            } else if (dayOfWeek.equals("fri")) {
                wakeupDate.setFri(Boolean.TRUE);
            } else if (dayOfWeek.equals("sat")) {
                wakeupDate.setSat(Boolean.TRUE);
            } else if (dayOfWeek.equals("sun")) {
                wakeupDate.setSun(Boolean.TRUE);
            }
        }
        return wakeupDate;
    }

    @Override
    public void deleteGroup(Long userId, Long groupId) {

        //Group group = groupRepository.findById(groupId).get(); 해당 그룹이 존재하는지 예외 처리도 들어가야 함
        groupRepository.deleteById(groupId);

    }



    @Override
    public UserGroup joinGroup(Long userId, GroupRequestDTO.JoinGroupDTO request) {
        Group group = groupRepository.findByParticipationCode(request.getParticipationCode()); // 참여 코드로 그룹 찾기
        // 위에서 굳이 참여 코드로 그룹을 찾을 필요가 있나? Path Variable로 넘어온 그룹 Id를 통해서 하면 안됨?
        // 참여 코드가 Path Variable로 넘어온 그룹 Id의 참여 코드인지 검증도 해줘야 함.
        // 참여 코드가 없을 시 예외 처리도 해줘야 함.
        // 유저가 존재하는 지 예외 처리 해줘야 함.

        UserGroup userGroup = UserGroupConverter.toUserGroup(userRepository.findById(userId).get(), request.getIsAgree()); // 유저 그룹 생성(그룹에 유저 넣기)
        userGroup.setGroup(group);
        // 그룹에 인원수가 다 찼다면 참여가 불가능한 예외 처리도 해줘야 함.

        return userGroup;
    }

}
