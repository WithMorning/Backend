package go.alarm.domain.repository;

import go.alarm.domain.entity.Group;
import go.alarm.domain.entity.User;
import go.alarm.domain.entity.UserGroup;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    List<UserGroup> findAllByUser(User user);
    List<UserGroup> findAllByGroup(Group group);

    @Query("SELECT ug.user FROM UserGroup ug WHERE ug.group = :group")
    List<User> findUserListByGroup(@Param("group") Group group);

    UserGroup findByUserAndGroup(User user, Group group);

}
