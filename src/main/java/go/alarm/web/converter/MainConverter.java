package go.alarm.web.converter;


import go.alarm.domain.entity.Group;
import go.alarm.domain.entity.User;
import go.alarm.domain.entity.UserGroup;
import go.alarm.web.dto.MainResponseDTO;
import go.alarm.web.dto.MainResponseDTO.UserDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainConverter {

    public static MainResponseDTO.UserDTO userDTO(User user){

        return MainResponseDTO.UserDTO.builder()
            .imageURL(user.getImageURL())
            .nickname(user.getNickname())
            .build();
            //.isWakeup() 이 2개는 서비스단에서 DB를 통해 알아올 수 있음.
            //.isDisturbBanMode()
    }

    public static MainResponseDTO.AlarmDTO alarmDTO(Group group){
        List<String> wakeupDateList = new ArrayList<>();

        if (group.getWakeupDate().getMon()) { //이렇게 하는게 맞나..?
            wakeupDateList.add("mon");
        }if (group.getWakeupDate().getTue()) {
            wakeupDateList.add("tue");
        }if (group.getWakeupDate().getWed()) {
            wakeupDateList.add("wed");
        }if (group.getWakeupDate().getThu()) {
            wakeupDateList.add("thu");
        }if (group.getWakeupDate().getFri()) {
            wakeupDateList.add("fri");
        }if (group.getWakeupDate().getSat()) {
            wakeupDateList.add("sat");
        }if (group.getWakeupDate().getSun()) {
            wakeupDateList.add("sun");
        }
/*
        List<UserGroup> userGroupList = group.getUserGroupList();
        List<User> userList = userGroupList.stream()
            .map(UserGroup::getUser) // UserGroup 객체에서 User 객체 추출
            .collect(Collectors.toList());

        List<UserDTO> userDTOList = userList.stream()
            .map(user -> MainConverter.userDTO(user)) // 추출한 User 객체를 UserDTO 객체로 변환
            .collect(Collectors.toList());*/


        return MainResponseDTO.AlarmDTO.builder()
            .name(group.getName())
            .wakeupTime(group.getWakeupTime())
            .wakeupDateList(wakeupDateList)
            //.isDisturbBanMode(group.get) 이 정보는 userGroup 엔티티에 있음. 이 정보를 빼올려면 컨버터가 아닌 서비스 단에서 쿼리문으로 가져와야할듯??
            //.userList(userDTOList)
            .memo(group.getMemo())
            .build();
    }

    public static MainResponseDTO.MainDTO MainDTO(List<Group> mainPage){

        List<MainResponseDTO.AlarmDTO> mainDTOList = mainPage.stream()
            .map(MainConverter::alarmDTO).collect(Collectors.toList());

        return MainResponseDTO.MainDTO.builder()
            //.connectorProfileURL(connectorProfileURL) // 컨버터에서 접속자 프로필 URL을 넣던지, 그냥 null로 보내주고 서비스 단에서 넣을지 정해야 함. 근데 난 서비스 단에서 넣을거임.
            .alarmList(mainDTOList)
            .listSize(mainPage.size())
            .build();
    }

}
