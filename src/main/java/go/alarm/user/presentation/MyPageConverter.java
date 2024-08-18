package go.alarm.user.presentation;

import go.alarm.wakeupdayofweek.domain.WakeUpDayOfWeek;
import go.alarm.user.domain.User;
import go.alarm.user.dto.response.MyPageResponse;
import java.util.ArrayList;
import java.util.List;

public class MyPageConverter {

    public static MyPageResponse getMyPage(User user){

        List<String> bedDayOfWeekList = getDayOfWeekList(user.getBedDayOfWeek());

        return MyPageResponse.builder()
            .userId(user.getId())
            .imageURL(user.getImageURL())
            .nickname(user.getNickname())
            .bedtime(user.getBedTime())
            .dayOfWeekList(bedDayOfWeekList)
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
