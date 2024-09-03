package go.alarm.group.service;

import go.alarm.group.domain.Group;
import go.alarm.group.domain.UserGroup;
import go.alarm.group.dto.request.GroupJoinRequest;
import go.alarm.group.dto.request.GroupRequest;

public interface GroupService {

    Group createGroup(Long userId, GroupRequest request);

    Group updateGroup(Long userId, Long groupId, GroupRequest request);

    void deleteGroup(Long userId, Long groupId);

    UserGroup joinGroup(Long userId, GroupJoinRequest request);

    void leaveGroup(Long userId, Long groupId);
}
