package go.alarm.group.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
public class GroupRequest {

    @NotBlank(message = "알람 그룹의 이름을 입력해주세요.")
    @Size(max = 15, message = "알람 그룹의 이름은 15자를 초과할 수 없습니다.")
    private String name;

    @NotNull(message = "알람 그룹의 기상 시간을 입력해주세요.")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime wakeupTime;

    @NotNull(message = "알람 그룹의 기상 요일을 입력해주세요.")
    private List<String> dayOfWeekList;

    @NotNull(message = "알람 그룹의 알람 수신 동의 여부를 입력해주세요.")
    private Boolean isAgree;

    @NotNull(message = "알람 그룹의 공지사항을 입력해주세요.")
    private String memo;
}
