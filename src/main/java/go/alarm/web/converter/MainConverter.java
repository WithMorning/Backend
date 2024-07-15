package go.alarm.web.converter;


import go.alarm.domain.entity.Group;
import go.alarm.domain.entity.User;
import go.alarm.domain.entity.UserGroup;
import go.alarm.web.dto.MainResponseDTO;
import go.alarm.domain.entity.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class MainConverter {

    public static MainResponseDTO.MainDTO mainDTO(String connectorProfileURL, List<MainResponseDTO.GroupDTO> groupDTOList) {
        return MainResponseDTO.MainDTO.builder()
            .connectorProfileURL(connectorProfileURL)
            .groupList(groupDTOList)
            .listSize(groupDTOList.size())
            .build();
    }

    public static MainResponseDTO.GroupDTO groupDTO(Group group, List<MainResponseDTO.UserDTO> userDTOList) {
        List<String> dayOfWeekList = getDayOfWeekList(group.getWakeUpDayOfWeek());

        return MainResponseDTO.GroupDTO.builder()
            .groupId(group.getId())
            .name(group.getName())
            .wakeupTime(group.getWakeupTime())
            .wakeUpDayOfWeekList(dayOfWeekList)
            .userList(userDTOList)
            .memo(group.getMemo())
            .build();
    }

    public static MainResponseDTO.UserDTO userDTO(User user, UserGroup userGroup) {
        return MainResponseDTO.UserDTO.builder()
            .userId(user.getId())
            .imageURL(user.getImageURL())
            .nickname(user.getNickname())
            .isWakeup(userGroup.getIsWakeup())
            .isDisturbBanMode(userGroup.getIsDisturbBanMode())
            .phone(user.getPhone())
            .build();
    }

    private static List<String> getDayOfWeekList(DayOfWeek dayOfWeek) {
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
