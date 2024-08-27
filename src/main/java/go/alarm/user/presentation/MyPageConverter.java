package go.alarm.user.presentation;

import go.alarm.wakeupdayofweek.domain.WakeUpDayOfWeek;
import go.alarm.user.domain.User;
import go.alarm.user.dto.response.MyPageResponse;
import java.util.ArrayList;
import java.util.List;

public class MyPageConverter {

    public static MyPageResponse getMyPage(User user){

        List<String> bedDayOfWeekList = getBedDayOfWeekList(user.getBedDayOfWeek());

        return MyPageResponse.builder()
            .userId(user.getId())
            .imageURL(user.getImageURL())
            .nickname(user.getNickname())
            .bedtime(user.getBedTime())
            .isAllowBedTimeAlarm(user.getIsAllowBedTimeAlarm())
            .dayOfWeekList(bedDayOfWeekList)
            .build();
    }

    private static List<String> getBedDayOfWeekList(WakeUpDayOfWeek bedDayOfWeek) {
        if(bedDayOfWeek == null){ // 취침 시간 설정을 안 해줬다면 null 반환
            return null;
        }

        List<String> bedDayOfWeekList = new ArrayList<>();

        if (bedDayOfWeek.getMon()) bedDayOfWeekList.add("mon");
        if (bedDayOfWeek.getTue()) bedDayOfWeekList.add("tue");
        if (bedDayOfWeek.getWed()) bedDayOfWeekList.add("wed");
        if (bedDayOfWeek.getThu()) bedDayOfWeekList.add("thu");
        if (bedDayOfWeek.getFri()) bedDayOfWeekList.add("fri");
        if (bedDayOfWeek.getSat()) bedDayOfWeekList.add("sat");
        if (bedDayOfWeek.getSun()) bedDayOfWeekList.add("sun");

        return bedDayOfWeekList;
    }

}
