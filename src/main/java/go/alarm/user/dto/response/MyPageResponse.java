package go.alarm.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "HH:mm")
    private LocalTime bedtime;
    private Boolean isAllowBedTimeAlarm;
    private List<String> dayOfWeekList;

}
