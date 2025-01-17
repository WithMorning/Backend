package go.alarm.home.presentation;


import go.alarm.group.domain.Group;
import go.alarm.user.domain.User;
import go.alarm.group.domain.UserGroup;
import go.alarm.home.dto.response.HomeResponse;
import go.alarm.wakeupdayofweek.domain.WakeUpDayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class HomeConverter {

    public static HomeResponse.HomeDTO homeDTO(User user, List<HomeResponse.GroupDTO> groupDTOList) {
        return HomeResponse.HomeDTO.builder()
            .connectorProfileURL(user.getImageURL())
            .connectorNickname(user.getNickname())
            .groupList(groupDTOList)
            .listSize(groupDTOList.size())
            .build();
    }

    public static HomeResponse.GroupDTO groupDTO(Group group, UserGroup userGroup, List<HomeResponse.UserDTO> userDTOList) {
        List<String> dayOfWeekList = getDayOfWeekList(group.getWakeUpDayOfWeek());

        return HomeResponse.GroupDTO.builder()
            .groupId(group.getId())
            .isDisturbBanGroup(userGroup.getIsDisturbBanMode())
            .name(group.getName())
            .wakeupTime(group.getWakeupTime())
            .dayOfWeekList(dayOfWeekList)
            .userList(userDTOList)
            .memo(group.getMemo())
            .participationCode(group.getParticipationCode())
            .build();
    }

    public static HomeResponse.UserDTO userDTO(User user, UserGroup userGroup) {
        System.out.print("@@@@@@@@@@@@@@" + userGroup.getIsHost());
        return HomeResponse.UserDTO.builder()
            .userId(user.getId())
            .imageURL(user.getImageURL())
            .nickname(user.getNickname())
            .isWakeup(userGroup.getIsWakeup())
            .isHost(userGroup.getIsHost())
            .isAgree(userGroup.getIsAgree())
            .isDisturbBanMode(userGroup.getIsDisturbBanMode())
            .phone(user.getPhone())
            .build();
    }

    private static List<String> getDayOfWeekList(WakeUpDayOfWeek dayOfWeek) {
        List<String> dayOfWeekList = new ArrayList<>();

        if (dayOfWeek.getMon()) dayOfWeekList.add("mon");
        if (dayOfWeek.getTue()) dayOfWeekList.add("tue");
        if (dayOfWeek.getWed()) dayOfWeekList.add("wed");
        if (dayOfWeek.getThu()) dayOfWeekList.add("thu");
        if (dayOfWeek.getFri()) dayOfWeekList.add("fri");
        if (dayOfWeek.getSat()) dayOfWeekList.add("sat");
        if (dayOfWeek.getSun()) dayOfWeekList.add("sun");

        return dayOfWeekList;
    }
}
