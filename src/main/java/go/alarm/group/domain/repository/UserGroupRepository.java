package go.alarm.group.domain.repository;

import go.alarm.group.domain.Group;
import go.alarm.user.domain.User;
import go.alarm.group.domain.UserGroup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    List<UserGroup> findAllByUser(User user);
    List<UserGroup> findAllByGroup(Group group);

    UserGroup findByUserAndGroup(User user, Group group);

    @Modifying
    @Query("""
            UPDATE UserGroup userGroup
            SET userGroup.status = 'DELETED'
            WHERE userGroup.user.id = :userId
            """)
    void deleteByUserId(@Param("userId") final Long userId);



}
