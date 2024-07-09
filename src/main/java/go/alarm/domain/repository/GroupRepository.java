package go.alarm.domain.repository;

import go.alarm.domain.entity.Group;
import go.alarm.domain.entity.WakeupDate;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Group findByParticipationCode(String participationCode);

    List<Group> findAllByWakeupTime(LocalTime now);
}
