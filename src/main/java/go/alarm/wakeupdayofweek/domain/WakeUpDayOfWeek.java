package go.alarm.wakeupdayofweek.domain;

import go.alarm.global.BaseEntity;
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
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WakeUpDayOfWeek extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean mon;
    private Boolean tue;
    private Boolean wed;
    private Boolean thu;
    private Boolean fri;
    private Boolean sat;
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