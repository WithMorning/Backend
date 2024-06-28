package go.alarm.web.controller;


import go.alarm.domain.entity.Group;
import go.alarm.domain.entity.User;
import go.alarm.domain.entity.UserGroup;
import go.alarm.response.BaseResponse;
import go.alarm.service.GroupService;
import go.alarm.web.converter.GroupConverter;
import go.alarm.web.dto.GroupRequestDTO;
import go.alarm.web.dto.GroupResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping
    @Operation(summary = "그룹 생성(알람 생성) API", description = "그룹(알람)을 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",description = "OK, 성공입니다.")
    })
    @Parameters({
        @Parameter(name = "userId", description = "유저의 아이디, header에 담아주시면 됩니다.")
    })
    public BaseResponse<GroupResponseDTO.CreateResultDto> createGroup(@RequestHeader(name = "userId") Long userId,
                                                                        @RequestBody @Valid GroupRequestDTO.CreateDTO request) {

        Group group = groupService.createGroup(userId, request);
        return new BaseResponse<>(GroupConverter.toCreateResultDTO(group));
    }

    @PostMapping("/{groupId}/join")
    @Operation(summary = "참여코드로 그룹에 참가하는 API", description = "참여 코드를 입력하여 그룹에 참가합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",description = "OK, 성공입니다.")
    })
    @Parameters({
        @Parameter(name = "groupId", description = "그룹의 아이디, path variable입니다."),
        @Parameter(name = "userId", description = "유저의 아이디, header에 담아주시면 됩니다.")
    })
    public BaseResponse<GroupResponseDTO.JoinResultDto> joinGroup(@RequestHeader(name = "userId") Long userId,
                                                                    @RequestBody @Valid GroupRequestDTO.JoinGroupDTO request) {

        UserGroup userGroup = groupService.joinGroup(userId, request);

        return new BaseResponse<>(GroupConverter.toJoinResultDTO(userGroup.getGroup(), userGroup.getUser()));
    }

}
