package go.alarm.web.controller;


import go.alarm.domain.entity.Group;
import go.alarm.domain.entity.UserGroup;
import go.alarm.response.BaseResponse;
import go.alarm.service.GroupService;
import go.alarm.web.converter.GroupConverter;
import go.alarm.web.dto.GroupRequestDTO;
import go.alarm.web.dto.GroupResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/groups")
@Slf4j
public class GroupController {

    private final GroupService groupService;

    @PostMapping// 그룹 생성 (= 알람 생성)
    public BaseResponse<GroupResponseDTO.CreateGroupDto> CreateGroup(@RequestHeader(name = "userId") Long userId,
                                                                    @RequestBody @Valid GroupRequestDTO.CreateGroupDTO request) {

        Group group = groupService.createGroup(userId, request);
        return new BaseResponse<>(GroupConverter.toCreateGroupDTO(group));
    }

    @PatchMapping("/{groupId}")// 그룹 수정 (= 알람 수정)
    public BaseResponse<GroupResponseDTO.UpdateGroupDto> UpdateGroup(@RequestHeader(name = "userId") Long userId,
                                                                    @PathVariable(name = "groupId") Long groupId,
                                                                    @RequestBody @Valid GroupRequestDTO.UpdateGroupDTO request) {

        Group group = groupService.updateGroup(userId, groupId, request);
        return new BaseResponse<>(GroupConverter.toUpdateGroupDTO(group));
    }

    @DeleteMapping("/{groupId}")// 그룹 삭제 (= 알람 삭제)
    public BaseResponse<GroupResponseDTO.DeleteGroupDto> DeleteGroup(@RequestHeader(name = "userId") Long userId,
                                                                    @PathVariable(name = "groupId") Long groupId) {

        groupService.deleteGroup(userId, groupId);
        return new BaseResponse<>(GroupConverter.toDeleteGroupDTO());
    }

    @PostMapping("/{groupId}/join")// 참여 코드로 참여
    public BaseResponse<GroupResponseDTO.JoinGroupDto> JoinGroup(@RequestHeader(name = "userId") Long userId,
                                                                @RequestBody @Valid GroupRequestDTO.JoinGroupDTO request) {

        UserGroup userGroup = groupService.joinGroup(userId, request);

        return new BaseResponse<>(GroupConverter.toJoinGroupDTO(userGroup.getGroup(), userGroup.getUser()));
    }

}
