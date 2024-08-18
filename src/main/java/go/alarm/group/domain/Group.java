package go.alarm.group.domain;


import go.alarm.wakeupdayofweek.domain.WakeUpDayOfWeek;
import go.alarm.global.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "alarm_group") // group + groups도 예약어라서 테이블 생성이 안됨.mysql
@Getter             // 예약어에 groups가 없어서 예약어 문제가 아니라고 생각하고 1시간 고민했는데 groups도 예약어임;;;
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Group extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(30)")
    private String name;

    @Column
    private LocalTime wakeupTime;

    @Column(columnDefinition = "VARCHAR(50)")
    private String memo;

    @Column(columnDefinition = "VARCHAR(100)")
    private String participationCode;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "day_of_week_id")
    private WakeUpDayOfWeek wakeUpDayOfWeek;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<UserGroup> userGroupList = new ArrayList<>();

    public void setWakeupTime(LocalTime wakeupTime){
        this.wakeupTime = wakeupTime;
    }

    public void setDayOfWeek(WakeUpDayOfWeek wakeUpDayOfWeek) {
        this.wakeUpDayOfWeek = wakeUpDayOfWeek;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setMemo(String memo){
        this.memo = memo;
    }


}