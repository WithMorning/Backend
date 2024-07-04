package go.alarm.web.converter;


import go.alarm.domain.entity.Group;
import go.alarm.domain.entity.User;
import go.alarm.domain.entity.UserGroup;
import go.alarm.web.dto.MainResponseDTO;
import go.alarm.domain.entity.WakeupDate;
import java.util.ArrayList;
import java.util.List;

public class MainConverter {

    public static MainResponseDTO.MainDTO mainDTO(String connectorProfileURL, List<MainResponseDTO.GroupDTO> groupDTOList) {
        return MainResponseDTO.MainDTO.builder()
            .connectorProfileURL(connectorProfileURL)
            .groupDTOList(groupDTOList)
            .listSize(groupDTOList.size())
            .build();
    }

    public static MainResponseDTO.GroupDTO groupDTO(Group group, List<MainResponseDTO.UserDTO> userDTOList) {
        List<String> wakeupDateList = getWakeupDateList(group.getWakeupDate());

        return MainResponseDTO.GroupDTO.builder()
            .groupId(group.getId())
            .name(group.getName())
            .wakeupTime(group.getWakeupTime())
            .wakeupDateList(wakeupDateList)
            .userDTOList(userDTOList)
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

    private static List<String> getWakeupDateList(WakeupDate wakeupDate) {
        List<String> wakeupDateList = new ArrayList<>();

        if (wakeupDate.getMon()) wakeupDateList.add("mon");
        if (wakeupDate.getTue()) wakeupDateList.add("tue");
        if (wakeupDate.getWed()) wakeupDateList.add("wed");
        if (wakeupDate.getThu()) wakeupDateList.add("thu");
        if (wakeupDate.getFri()) wakeupDateList.add("fri");
        if (wakeupDate.getSat()) wakeupDateList.add("sat");
        if (wakeupDate.getSun()) wakeupDateList.add("sun");

        return wakeupDateList;
    }
}
