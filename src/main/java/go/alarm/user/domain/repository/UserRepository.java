package go.alarm.user.domain.repository;

import go.alarm.user.domain.User;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialLoginId(String socialLoginId);
    @Modifying
    @Query("""
            UPDATE Users user
            SET user.status = 'DELETED'
            WHERE user.id = :userId
            """)
    void softDeleteByUserId(@Param("userId") final Long userId);

    List<User> findAllByBedTime(LocalTime now);

}
