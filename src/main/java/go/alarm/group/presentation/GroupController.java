package go.alarm.group.presentation;


import go.alarm.auth.Auth;
import go.alarm.auth.UesrOnly;
import go.alarm.auth.domain.Accessor;
import go.alarm.group.domain.Group;
import go.alarm.group.domain.UserGroup;
import go.alarm.global.response.SuccessResponse;
import go.alarm.group.dto.request.GroupJoinRequest;
import go.alarm.group.dto.response.GroupJoinResponse;
import go.alarm.group.dto.response.GroupResponse;
import go.alarm.group.service.GroupService;
import go.alarm.group.dto.request.GroupRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/groups")
@Slf4j
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    @UesrOnly
    @Operation(summary = "그룹 생성(알람 생성) API", description = "그룹(알람)을 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",description = "OK, 성공입니다.")
    })
    public SuccessResponse<GroupResponse> createGroup(
        @Auth final Accessor accessor,
        @RequestBody @Valid GroupRequest request) {

        Group group = groupService.createGroup(accessor.getUserId(), request);
        return new SuccessResponse<>(GroupConverter.createGroup(group));
    }

    @PatchMapping("/{groupId}")
    @UesrOnly
    @Operation(summary = "그룹 수정(알람 수정) API", description = "그룹(알람)을 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",description = "OK, 성공입니다.")
    })
    public SuccessResponse<Void> updateGroup(
        @Auth final Accessor accessor,
        @PathVariable(name = "groupId") Long groupId,
        @RequestBody @Valid GroupRequest request) {

        groupService.updateGroup(accessor.getUserId(), groupId, request);
        return new SuccessResponse<>();
    }

    @DeleteMapping("/{groupId}")
    @UesrOnly
    @Operation(summary = "그룹 삭제(알람 삭제) API", description = "그룹(알람)을 삭제합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",description = "OK, 성공입니다.")
    })
    public SuccessResponse<Void> deleteGroup(
        @Auth final Accessor accessor,
        @PathVariable(name = "groupId") Long groupId) {

        groupService.deleteGroup(accessor.getUserId(), groupId);
        return new SuccessResponse<>();
    }

    @PostMapping("/join")
    @UesrOnly
    @Operation(summary = "참여코드로 그룹에 참가하는 API", description = "참여 코드를 입력하여 그룹에 참가합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",description = "OK, 성공입니다.")
    })
    @Parameters({
        @Parameter(name = "groupId", description = "그룹의 아이디, path variable입니다."),
    })
    public SuccessResponse<GroupJoinResponse> joinGroup(
        @Auth final Accessor accessor,
        @RequestBody @Valid GroupJoinRequest request) {

        UserGroup userGroup = groupService.joinGroup(accessor.getUserId(), request);

        return new SuccessResponse<>(GroupConverter.joinGroup(userGroup.getGroup(), userGroup.getUser()));
    }

    @DeleteMapping("/{groupId}/leave") // 테스트 해봐야 함!!!
    @UesrOnly
    @Operation(summary = "알람 그룹 나가기(참여자 플로우) API", description = "알람 그룹을 나갑니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "OK, 성공입니다.")
    })
    public SuccessResponse<Void> leaveGroup(
        @Auth final Accessor accessor,
        @PathVariable(name = "groupId") Long groupId) {

        groupService.leaveGroup(accessor.getUserId(), groupId);
        return new SuccessResponse<>();
    }

}
