package go.alarm.web.controller;


import go.alarm.domain.entity.Group;
import go.alarm.domain.entity.User;
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
    public BaseResponse<GroupResponseDTO.CreateResultDto> CreateGroup
        (@RequestHeader(name = "userId") Long userId,
            @RequestBody @Valid GroupRequestDTO.CreateDTO request) {

        Group group = groupService.createGroup(userId, request);
        return new BaseResponse<>(GroupConverter.toCreateResultDTO(group));
    }

    @PostMapping("/{groupId}/memo") // 메모 생성
    public BaseResponse<GroupResponseDTO.CreateResultDto> CreateMemo
        (@RequestHeader(name = "userId") Long userId,
            @PathVariable(name = "groupId") Long groupId,
            @RequestBody @Valid GroupRequestDTO.CreateMemoDTO request) {

        Group group = groupService.createMemo(userId, groupId, request);
        return new BaseResponse<>(GroupConverter.toCreateResultDTO(group));
    }

    @PostMapping("/{groupId}/join")// 참여 코드로 참여
    public BaseResponse<GroupResponseDTO.JoinResultDto> JoinGroup
        (@RequestHeader(name = "userId") Long userId,
            @RequestBody @Valid GroupRequestDTO.JoinGroupDTO request) {

        UserGroup userGroup = groupService.joinGroup(userId, request);

        return new BaseResponse<>(GroupConverter.toJoinResultDTO(userGroup.getGroup(), userGroup.getUser()));
    }

}
