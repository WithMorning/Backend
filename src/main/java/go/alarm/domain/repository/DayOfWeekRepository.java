package go.alarm.domain.repository;

import go.alarm.domain.entity.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DayOfWeekRepository extends JpaRepository<DayOfWeek, Long> {

}
