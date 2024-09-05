package go.alarm.user.service;

import go.alarm.image.ImageUploader;
import go.alarm.user.dto.request.UserProfileRequest;
import go.alarm.wakeupdayofweek.domain.WakeUpDayOfWeek;
import go.alarm.user.domain.User;
import go.alarm.wakeupdayofweek.domain.repository.WakeUpDayOfWeekRepository;
import go.alarm.user.domain.repository.UserRepository;
import go.alarm.wakeupdayofweek.presentation.WakeUpDayOfWeekConverter;
import go.alarm.user.dto.request.UserBedTimeRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WakeUpDayOfWeekRepository wakeUpDayOfWeekRepository;
    private final ImageUploader imageUploader;


    @Override
    public User getUser(Long userId) {

        return userRepository.findById(userId).get();
    }

    @Override
    public User setBedTime(Long userId, UserBedTimeRequest request) {

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
        user.setBedTime(request.getBedTime());

        return user;
    }

    @Override
    public void setProfile(Long userId, UserProfileRequest request) {

        User user = userRepository.findById(userId).get();

        user.setFcmToken(request.getFcmToken());
        user.setNickname(request.getNickname());

        MultipartFile image = request.getImage();
        log.warn("image는 >> " + image);


        if (image != null){
            String uuid = UUID.randomUUID().toString();
            String imageUrl = imageUploader.uploadFile(
                imageUploader.generateUserProfileKeyName(uuid), image);
            user.setImageURL(imageUrl);
        }
    }

}
