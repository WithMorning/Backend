package go.alarm.web.converter;


import go.alarm.domain.entity.Group;
import go.alarm.domain.entity.WakeupDate;
import java.util.List;

public class WakeupDateConverter {

    public static WakeupDate toWakeupDate(Group group, List<String> wakeupDateList) {

        Boolean isMon = Boolean.FALSE,
            isTue = Boolean.FALSE, isWed = Boolean.FALSE,
            isThu = Boolean.FALSE, isFri = Boolean.FALSE,
            isSat = Boolean.FALSE, isSun = Boolean.FALSE;

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
        return WakeupDate.builder()
            .group(group)
            .mon(isMon)
            .tue(isTue)
            .wed(isWed)
            .thu(isThu)
            .fri(isFri)
            .sat(isSat)
            .sun(isSun)
            .build();
    }
}