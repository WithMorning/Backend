package go.alarm.user.service;

import go.alarm.wakeupdayofweek.domain.WakeUpDayOfWeek;
import go.alarm.user.domain.User;
import go.alarm.wakeupdayofweek.domain.repository.WakeUpDayOfWeekRepository;
import go.alarm.user.domain.repository.UserRepository;
import go.alarm.wakeupdayofweek.presentation.WakeUpDayOfWeekConverter;
import go.alarm.user.dto.UserRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WakeUpDayOfWeekRepository wakeUpDayOfWeekRepository;


    @Override
    public User getUser(Long userId) {

        User user = userRepository.findById(userId).get();

        return user;
    }

    @Override
    public User setBedTime(Long userId, UserRequestDTO.SetBedTimeDTO request) {
        User user = userRepository.findById(userId).get();

        if(user.getBedDayOfWeek() != null){
            wakeUpDayOfWeekRepository.delete(user.getBedDayOfWeek());
        }

        WakeUpDayOfWeek dayOfWeek = WakeUpDayOfWeekConverter.toDayOfWeek(request.getBedDayOfWeekList());

        for (String day : request.getBedDayOfWeekList()){
            if (day.equals("mon")) {
                dayOfWeek.setMon(Boolean.TRUE);
            } else if (day.equals("tue")) {
                dayOfWeek.setTue(Boolean.TRUE);
            } else if (day.equals("wed")) {
                dayOfWeek.setWed(Boolean.TRUE);
            } else if (day.equals("thu")) {
                dayOfWeek.setThu(Boolean.TRUE);
            } else if (day.equals("fri")) {
                dayOfWeek.setFri(Boolean.TRUE);
            } else if (day.equals("sat")) {
                dayOfWeek.setSat(Boolean.TRUE);
            } else if (day.equals("sun")) {
                dayOfWeek.setSun(Boolean.TRUE);
            }
        }

        user.setDayOfWeek(dayOfWeek);
        user.setIsAllowBedTimeAlarm(request.getIsAllowBedTimeAlarm());

        return user;
    }

}
