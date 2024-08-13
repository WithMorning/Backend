package go.alarm.domain.repository;

import go.alarm.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialLoginId(String socialLoginId);

    boolean existsByNickname(String nickname);

    @Modifying
    @Query("""
            UPDATE Users user
            SET user.status = 'DELETED'
            WHERE user.id = :userId
            """)
    void deleteByUserId(@Param("userId") final Long userId);

}
