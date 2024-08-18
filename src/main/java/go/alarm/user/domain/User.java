package go.alarm.user.domain;

import static jakarta.persistence.EnumType.STRING;

import go.alarm.group.domain.UserGroup;
import go.alarm.wakeupdayofweek.domain.WakeUpDayOfWeek;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity(name = "Users")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
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

    @Column
    private Boolean isAllowBedTimeAlarm;

    @Column
    private LocalTime bedTime;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

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
