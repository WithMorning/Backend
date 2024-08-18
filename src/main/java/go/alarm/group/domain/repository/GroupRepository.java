package go.alarm.group.domain.repository;

import go.alarm.group.domain.Group;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Group findByParticipationCode(String participationCode);

    List<Group> findAllByWakeupTime(LocalTime now);

}
