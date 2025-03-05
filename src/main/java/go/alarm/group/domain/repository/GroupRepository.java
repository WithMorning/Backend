package go.alarm.group.domain.repository;

import go.alarm.group.domain.Group;
import jakarta.transaction.Transactional;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Group findByParticipationCode(String participationCode);

    List<Group> findAllByWakeupTime(LocalTime now);

    @Modifying
    @Query("""
            UPDATE alarm_group group
            SET group.status = 'DELETED'
            WHERE group.id = :groupId
            """)
    void softDeleteById(@Param("groupId") final Long groupId);

    @Modifying
    @Transactional
    @Query("""
            DELETE FROM alarm_group g 
            WHERE g IN (
                SELECT ug.group 
                FROM UserGroup ug 
                WHERE ug.user.id = :userId AND ug.isHost = true
            )
            """)
    void deleteGroupsCreatedByUser(@Param("userId") final Long userId);
}
