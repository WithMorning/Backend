package go.alarm.login.domain.repository;

import go.alarm.login.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    RefreshToken findByUserId(Long userId);

}
