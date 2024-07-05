package go.alarm.web.converter;


import go.alarm.domain.entity.Group;
import go.alarm.domain.entity.WakeupDate;
import java.util.List;

public class WakeupDateConverter {

    public static WakeupDate toWakeupDate(Group group, List<String> wakeupDateList) {

        WakeupDateResult wakeupDateResult = getWakeupDate(wakeupDateList);
        return WakeupDate.builder()
            .group(group)
            .mon(wakeupDateResult.isMon())
            .tue(wakeupDateResult.isTue())
            .wed(wakeupDateResult.isWed())
            .thu(wakeupDateResult.isThu())
            .fri(wakeupDateResult.isFri())
            .sat(wakeupDateResult.isSat())
            .sun(wakeupDateResult.isSun())
            .build();
    }

    private static WakeupDateResult getWakeupDate(List<String> wakeupDateList) {
        Boolean isMon = Boolean.FALSE,
            isTue = Boolean.FALSE, isWed = Boolean.FALSE,
            isThu = Boolean.FALSE, isFri = Boolean.FALSE,
            isSat = Boolean.FALSE, isSun = Boolean.FALSE;

        // 이 부분을 메소드로 빼서 main컨버터랑 여기랑 같은 코드로 쓸 수도 있지 않을까?
        for (String dayOfWeek : wakeupDateList) {
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
        WakeupDateResult wakeupDateResult = new WakeupDateResult(isMon, isTue, isWed, isThu, isFri, isSat, isSun);
        return wakeupDateResult;
    }

    private record WakeupDateResult(Boolean isMon, Boolean isTue, Boolean isWed, Boolean isThu, Boolean isFri, Boolean isSat, Boolean isSun) {
    }
}