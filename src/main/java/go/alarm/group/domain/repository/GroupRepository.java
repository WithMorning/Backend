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

    @Transactional
    @Modifying // executeUpdate, executeDelete 를 사용하기 위해 필요함.
    @Query("delete from Group g where g in (select ug.group from UserGroup ug where ug.user.id = :userId and ug.isHost = true)")
    void deleteGroupsCreatedByUserId(@Param("userId") Long userId);

}
