package go.alarm.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
public class WakeupDate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    private Boolean mon;

    private Boolean tue;

    private Boolean wed;

    private Boolean thu;

    private Boolean fri;

    private Boolean sat;

    private Boolean sun;

    public void setGroup(Group group) {
        if (this.group != null) {
            Group previousGroup = this.group;
            this.group = null;
            previousGroup.setWakeupDate(null); // 1:1 관계에서 반대쪽 객체의 참조 해제, setGroup, setWakeupDate 다시 봐야할듯.
        }
        this.group = group;
    }
}