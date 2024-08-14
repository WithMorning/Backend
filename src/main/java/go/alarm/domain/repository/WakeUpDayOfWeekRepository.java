package go.alarm.domain.repository;

import go.alarm.domain.entity.WakeUpDayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WakeUpDayOfWeekRepository extends JpaRepository<WakeUpDayOfWeek, Long> {

}
