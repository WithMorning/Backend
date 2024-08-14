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
public class WakeUpDayOfWeek extends BaseEntity {

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