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

    List<UserGroup> findAllByUserOrderByCreatedAtDesc(User user);
    List<UserGroup> findAllByGroup(Group group);
    UserGroup findByUserAndGroup(User user, Group group);

    @Query("""
            SELECT COUNT(DISTINCT ug.user)
            FROM UserGroup ug
            WHERE ug.group = :group
            """)
    Long findHeadCount(Group group); // 인원수 찾기

    @Modifying
    @Query("""
            UPDATE UserGroup userGroup
            SET userGroup.status = 'DELETED'
            WHERE userGroup.user.id = :userId
            """)
    void softDeleteByUserId(@Param("userId") final Long userId);

    void deleteByUserId(Long userId);

    @Modifying
    @Query("""
            UPDATE UserGroup ug
            SET ug.isHost = true
            WHERE ug.id = :userGroupId AND ug.user.id = :userId
            """)
    void changeHost(@Param("userGroupId") Long userGroupId, @Param("userId") Long userId);



}
