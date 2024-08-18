package go.alarm.wakeupdayofweek.domain.repository;

import go.alarm.wakeupdayofweek.domain.WakeUpDayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WakeUpDayOfWeekRepository extends JpaRepository<WakeUpDayOfWeek, Long> {

}
