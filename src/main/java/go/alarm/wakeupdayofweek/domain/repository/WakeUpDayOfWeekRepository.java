package go.alarm.wakeupdayofweek.domain.repository;

import go.alarm.wakeupdayofweek.domain.WakeUpDayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WakeUpDayOfWeekRepository extends JpaRepository<WakeUpDayOfWeek, Long> {

    @Modifying
    @Query("""
            UPDATE WakeUpDayOfWeek wakeUpDayOfWeek
            SET wakeUpDayOfWeek.status = 'DELETED'
            WHERE wakeUpDayOfWeek.id = :dayOfWeekId
            """)
    void deleteById(@Param("dayOfWeekId") final Long dayOfWeekId);

}
