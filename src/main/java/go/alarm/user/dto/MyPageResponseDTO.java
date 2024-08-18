package go.alarm.user.dto;

import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MyPageResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class myPageDTO{
        Long userId;
        String imageURL;
        String nickname;
        LocalTime bedtime;
        List<String> dayOfWeekList;
    }

}
