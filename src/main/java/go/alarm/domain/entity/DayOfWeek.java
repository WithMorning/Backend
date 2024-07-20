package go.alarm.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DayOfWeek extends BaseEntity { // 기존 엔티티명 = wakeupDate
    // 여기에 wakeupTime, bedTime도 추가해서 하나의 클래스로 관리해도 좋을듯?

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private Boolean mon;
    @Setter
    private Boolean tue;
    @Setter
    private Boolean wed;
    @Setter
    private Boolean thu;
    @Setter
    private Boolean fri;
    @Setter
    private Boolean sat;
    @Setter
    private Boolean sun;


    public void resetDayOfWeek(){
        this.mon = Boolean.FALSE;
        this.tue = Boolean.FALSE;
        this.wed = Boolean.FALSE;
        this.thu = Boolean.FALSE;
        this.fri = Boolean.FALSE;
        this.sat = Boolean.FALSE;
        this.sun = Boolean.FALSE;
    }
}