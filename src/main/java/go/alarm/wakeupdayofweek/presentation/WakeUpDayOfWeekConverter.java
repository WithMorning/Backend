package go.alarm.wakeupdayofweek.presentation;


import go.alarm.wakeupdayofweek.domain.WakeUpDayOfWeek;
import java.util.List;

public class WakeUpDayOfWeekConverter {

    private record DayOfWeekResult(Boolean isMon, Boolean isTue, Boolean isWed,
                                   Boolean isThu, Boolean isFri, Boolean isSat, Boolean isSun) {
    }

    public static WakeUpDayOfWeek toDayOfWeek(List<String> dayOfWeekList) {

        DayOfWeekResult dayOfWeekResult = getDayOfWeek(dayOfWeekList);
        return WakeUpDayOfWeek.builder()
            .mon(dayOfWeekResult.isMon())
            .tue(dayOfWeekResult.isTue())
            .wed(dayOfWeekResult.isWed())
            .thu(dayOfWeekResult.isThu())
            .fri(dayOfWeekResult.isFri())
            .sat(dayOfWeekResult.isSat())
            .sun(dayOfWeekResult.isSun())
            .build();
    }

    private static DayOfWeekResult getDayOfWeek(List<String> dayOfWeekList) {

        Boolean isMon = Boolean.FALSE, isTue = Boolean.FALSE, isWed = Boolean.FALSE,
            isThu = Boolean.FALSE, isFri = Boolean.FALSE, isSat = Boolean.FALSE, isSun = Boolean.FALSE;

        // 이 부분을 메소드로 빼서 main컨버터랑 여기랑 같은 코드로 쓸 수도 있지 않을까?
        for (String dayOfWeek : dayOfWeekList) {
            if (dayOfWeek.equals("mon")) {
                isMon = Boolean.TRUE;
            } else if (dayOfWeek.equals("tue")) {
                isTue = Boolean.TRUE;
            } else if (dayOfWeek.equals("wed")) {
                isWed = Boolean.TRUE;
            } else if (dayOfWeek.equals("thu")) {
                isThu = Boolean.TRUE;
            } else if (dayOfWeek.equals("fri")) {
                isFri = Boolean.TRUE;
            } else if (dayOfWeek.equals("sat")) {
                isSat = Boolean.TRUE;
            } else if (dayOfWeek.equals("sun")) {
                isSun = Boolean.TRUE;
            }
        }
        return new DayOfWeekResult(isMon, isTue, isWed, isThu, isFri, isSat, isSun);
    }
}