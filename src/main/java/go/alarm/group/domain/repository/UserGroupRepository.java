package go.alarm.group.domain.repository;

import go.alarm.group.domain.Group;
import go.alarm.user.domain.User;
import go.alarm.group.domain.UserGroup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    List<UserGroup> findAllByUser(User user);
    List<UserGroup> findAllByGroup(Group group);

}
