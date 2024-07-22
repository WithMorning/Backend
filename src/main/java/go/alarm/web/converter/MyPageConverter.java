package go.alarm.web.converter;

import go.alarm.domain.entity.DayOfWeek;
import go.alarm.domain.entity.User;
import go.alarm.web.dto.MyPageResponseDTO;
import java.util.ArrayList;
import java.util.List;

public class MyPageConverter {

    public static MyPageResponseDTO.myPageDTO toMyPageDTO(User user){

        List<String> bedDayOfWeekList = getDayOfWeekList(user.getBedDayOfWeek());

        return MyPageResponseDTO.myPageDTO.builder()
            .userId(user.getId())
            .imageURL(user.getImageURL())
            .nickname(user.getNickname())
            .bedtime(user.getBedTime())
            .dayOfWeekList(bedDayOfWeekList)
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
