package go.alarm.group.domain;

import go.alarm.global.BaseEntity;
import go.alarm.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserGroup extends BaseEntity { // UserGroup이라는 클래스명보다 AlarmGroup이 더 맞는 것 같은데?? 검토해봐야 함.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(columnDefinition = "VARCHAR(30)")
    private String phone;

    private Boolean isWakeup;

    private Boolean isAgree; // 전화번호 사용 동의 여부

    private Boolean isDisturbBanMode; // 방해금지모드 여부 >> True:방해금지모드 on, False:방해금지모드 off

    public void setGroup(Group group){
        if (this.group != null)
            group.getUserGroupList().remove(this);
        this.group = group;
        group.getUserGroupList().add(this);
    }

    public void setDisturbBanMode(Boolean disturbBanMode) {
        isDisturbBanMode = disturbBanMode;
    }

    public void setWakeup(Boolean wakeup) {
        isWakeup = wakeup;
    }

    public void setAgree(Boolean agree) {
        isAgree = agree;
    }
}