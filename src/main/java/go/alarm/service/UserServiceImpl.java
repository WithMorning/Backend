package go.alarm.service;

import go.alarm.domain.entity.DayOfWeek;
import go.alarm.domain.entity.User;
import go.alarm.domain.repository.DayOfWeekRepository;
import go.alarm.domain.repository.UserRepository;
import go.alarm.web.converter.dayOfWeekConverter;
import go.alarm.web.dto.request.UserRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DayOfWeekRepository dayOfWeekRepository;


    @Override
    public User getUser(Long userId) {

        User user = userRepository.findById(userId).get();

        return user;
    }

    @Override
    public User setBedTime(Long userId, UserRequestDTO.SetBedTimeDTO request) {
        User user = userRepository.findById(userId).get();

        if(user.getBedDayOfWeek() != null){
            dayOfWeekRepository.delete(user.getBedDayOfWeek());
        }

        DayOfWeek dayOfWeek = dayOfWeekConverter.toDayOfWeek(request.getBedDayOfWeekList());

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
