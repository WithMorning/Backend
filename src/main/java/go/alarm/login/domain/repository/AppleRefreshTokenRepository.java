package go.alarm.login.domain.repository;

import go.alarm.login.domain.AppleRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppleRefreshTokenRepository extends JpaRepository<AppleRefreshToken, String> {

    AppleRefreshToken findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
