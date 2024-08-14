package go.alarm.domain.entity;

import static jakarta.persistence.EnumType.STRING;

import go.alarm.domain.enums.UserState;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
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

@Entity(name = "Users")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(30)", nullable = false)
    private String socialLoginId;

    @Column(unique = true)
    private String fcmToken;

    @Column(columnDefinition = "VARCHAR(30)")
    private String nickname;

    @Column(columnDefinition = "VARCHAR(50)")
    private String email;

    @Column(columnDefinition = "VARCHAR(30)")
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String imageURL;

    @Enumerated(value = STRING)
    private UserState status;

    @Column
    private Boolean isCertify;

    @OneToOne(mappedBy = "sender", cascade = CascadeType.ALL)
    private WakeUpDayOfWeek wakeUpDayOfWeek;

    @Column
    private Boolean isAllowBedTimeAlarm;

    @Column
    private LocalTime bedTime;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "day_of_week_id")
    private WakeUpDayOfWeek bedDayOfWeek;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserGroup> userGroupList = new ArrayList<>();

    public void setDayOfWeek(WakeUpDayOfWeek bedDayOfWeek) {
        this.bedDayOfWeek = bedDayOfWeek;
    }
    public void setIsAllowBedTimeAlarm(Boolean isAllowBedTimeAlarm){
        this.isAllowBedTimeAlarm = isAllowBedTimeAlarm;
    }

}
