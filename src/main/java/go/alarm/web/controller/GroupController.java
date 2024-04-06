package go.alarm.web.controller;


import go.alarm.domain.entity.Group;
import go.alarm.response.BaseResponse;
import go.alarm.service.GroupCommandService;
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

    private final GroupCommandService groupCommandService;

    @PostMapping
    public BaseResponse<GroupResponseDTO.CreateResultDto> CreateGroup
        (@RequestHeader(name = "userId") Long userId,
            @RequestBody @Valid GroupRequestDTO.CreateDTO request) {

        Group group = groupCommandService.createGroup(userId, request);
        return new BaseResponse<>(GroupConverter.toCreateResultDTO(group));
    }

    @PostMapping("/{groupId}/memo")
    public BaseResponse<GroupResponseDTO.CreateResultDto> CreateMemo
        (@RequestHeader(name = "userId") Long userId,
            @PathVariable(name = "groupId") Long groupId,
            @RequestBody @Valid GroupRequestDTO.CreateMemoDTO request) {

        Group group = groupCommandService.createMemo(userId, groupId, request);
        return new BaseResponse<>(GroupConverter.toCreateResultDTO(group));
    }

}
