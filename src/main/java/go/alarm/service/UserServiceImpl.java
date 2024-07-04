package go.alarm.service;

import go.alarm.domain.entity.User;
import go.alarm.domain.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUser(Long userId) {

        User user = userRepository.findById(userId).get();

        return user;
    }


}
