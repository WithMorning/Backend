package go.alarm.group.service;

import go.alarm.group.domain.Group;
import go.alarm.group.domain.UserGroup;
import go.alarm.group.dto.request.GroupJoinRequest;
import go.alarm.wakeupdayofweek.domain.WakeUpDayOfWeek;
import go.alarm.group.domain.repository.GroupRepository;
import go.alarm.user.domain.repository.UserRepository;
import go.alarm.group.presentation.GroupConverter;
import go.alarm.wakeupdayofweek.presentation.WakeUpDayOfWeekConverter;
import go.alarm.group.dto.request.GroupRequest;
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

    @Override
    public Group createGroup(Long userId, GroupRequest request) {

        Group group = GroupConverter.toGroup(request); // 그룹 생성
        WakeUpDayOfWeek dayOfWeek = WakeUpDayOfWeekConverter.toDayOfWeek(request.getDayOfWeekList()); // 기상 요일 설정
        group.setDayOfWeek(dayOfWeek);

        UserGroup userGroup = GroupConverter.toUserGroup(userRepository.findById(userId).get(), request.getIsAgree()); // 유저 그룹 생성
        userGroup.setGroup(group); // userGroup을 userGroupRepository.save로 저장해야 하나..?
        // 아니다. setGroup을 들어가보면 group.~~.add로 userGroup객체를 넣어준다.


        // 여기다가 user - userGroup 매핑을 해줘야 함...? 로직이 너무 거대해지는데..? 일단 이건 keep..

        // 여기에 UserGroup도 같이 생성해주는 로직이 추가되어야 함.
        // 이후 인원 초대를 할 경우 UserGroup 테이블에 new 유저 데이터를 추가
        // 만약 비회원이 초대코드를 받았다면 회원가입(User 테이블에 삽입) + UserGroup에도 함께 삽입
        
        return groupRepository.save(group);
    }

    @Override
    public Group updateGroup(Long userId, Long groupId, GroupRequest request) {

        Group group = groupRepository.findById(groupId).get();

        group.setWakeupTime(request.getWakeupTime());
        updateDayOfWeek(group.getWakeUpDayOfWeek(), request.getDayOfWeekList());
        group.setName(request.getName());
        group.setMemo(request.getMemo());

        return group;
    }

    public WakeUpDayOfWeek updateDayOfWeek(WakeUpDayOfWeek dayOfWeek, List<String> dayOfWeekList){

        dayOfWeek.resetDayOfWeek(); // 기존 기상 요일 리셋

        for (String day : dayOfWeekList) {
            if (day.equals("mon")) {
                dayOfWeek.setMon(Boolean.TRUE);
            } else if (day.equals("tue")) {
                dayOfWeek.setTue(Boolean.TRUE);
            } else if (day.equals("wed")) {
                dayOfWeek.setWed(Boolean.TRUE);
            } else if (day.equals("thu")) {
                dayOfWeek.setThu(Boolean.TRUE);
            } else if (day.equals("fri")) {
                dayOfWeek.setFri(Boolean.TRUE);
            } else if (day.equals("sat")) {
                dayOfWeek.setSat(Boolean.TRUE);
            } else if (day.equals("sun")) {
                dayOfWeek.setSun(Boolean.TRUE);
            }
        }
        return dayOfWeek;
    }

    @Override
    public void deleteGroup(Long userId, Long groupId) {

        //Group group = groupRepository.findById(groupId).get(); 해당 그룹이 존재하는지 예외 처리도 들어가야 함
        groupRepository.deleteById(groupId);

    }



    @Override
    public UserGroup joinGroup(Long userId, GroupJoinRequest request) {
        Group group = groupRepository.findByParticipationCode(request.getParticipationCode()); // 참여 코드로 그룹 찾기
        // 위에서 굳이 참여 코드로 그룹을 찾을 필요가 있나? Path Variable로 넘어온 그룹 Id를 통해서 하면 안됨?
        // 참여 코드가 Path Variable로 넘어온 그룹 Id의 참여 코드인지 검증도 해줘야 함.
        // 참여 코드가 없을 시 예외 처리도 해줘야 함.
        // 유저가 존재하는 지 예외 처리 해줘야 함.

        UserGroup userGroup = GroupConverter.toUserGroup(userRepository.findById(userId).get(), request.getIsAgree()); // 유저 그룹 생성(그룹에 유저 넣기)
        userGroup.setGroup(group);
        // 그룹에 인원수가 다 찼다면 참여가 불가능한 예외 처리도 해줘야 함.

        return userGroup;
    }

}
