package go.alarm.user.dto.response;

import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyPageResponse {

    private Long userId;
    private String imageURL;
    private String nickname;
    private LocalTime bedtime;
    private Boolean isAllowBedTimeAlarm;
    private List<String> dayOfWeekList;

}
