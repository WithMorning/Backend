package go.alarm.user.service;

import static go.alarm.global.response.ResponseCode.ERROR_SEND_SMS;
import static go.alarm.global.response.ResponseCode.FAIL_SEND_SMS;
import static go.alarm.global.response.ResponseCode.NOT_FOUND_GROUP_ID;
import static go.alarm.global.response.ResponseCode.UNMATCHED_CODE;

import go.alarm.global.response.exception.BadRequestException;
import go.alarm.global.response.exception.SmsSendException;
import go.alarm.global.response.exception.VerifyCodeException;
import go.alarm.group.domain.Group;
import go.alarm.group.domain.UserGroup;
import go.alarm.group.domain.repository.GroupRepository;
import go.alarm.group.domain.repository.UserGroupRepository;
import go.alarm.image.ImageUploader;
import go.alarm.user.dto.request.UserProfileRequest;
import go.alarm.wakeupdayofweek.domain.WakeUpDayOfWeek;
import go.alarm.user.domain.User;
import go.alarm.wakeupdayofweek.domain.repository.WakeUpDayOfWeekRepository;
import go.alarm.user.domain.repository.UserRepository;
import go.alarm.wakeupdayofweek.presentation.WakeUpDayOfWeekConverter;
import go.alarm.user.dto.request.UserBedTimeRequest;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupRepository groupRepository;
    private final WakeUpDayOfWeekRepository wakeUpDayOfWeekRepository;
    private final ImageUploader imageUploader;
    private final DefaultMessageService messageService;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${coolsms.from.number}")
    private String fromNumber;
    private static final long VERIFICATION_CODE_EXPIRY = 3; // 3 minutes

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
    public void setProfile(Long userId, UserProfileRequest request, MultipartFile image) {

        User user = userRepository.findById(userId).get();

        user.setFcmToken(request.getFcmToken());
        user.setNickname(request.getNickname());

        log.warn("image는 >> " + image);

        if (image != null){
            String uuid = UUID.randomUUID().toString();
            String imageUrl = imageUploader.uploadFile(imageUploader.generateUserProfileKeyName(uuid), image);
            user.setImageURL(imageUrl);
        }
        else {
            user.setImageURL(null);
        }
    }

    @Override
    public void sendVerificationCode(String phone) {
        String code = generateRandomCode();
        Message message = setMessage(phone, code);

        redisTemplate.opsForValue().set(phone, code, VERIFICATION_CODE_EXPIRY, TimeUnit.MINUTES);

        try {
            messageService.send(message);
        } catch (NurigoMessageNotReceivedException exception) { // 발송 실패 처리
            log.error("(NurigoMessageNotReceivedException) 휴대폰 문자 전송 에러 상세 내용: " + exception.getMessage());
            throw new SmsSendException(FAIL_SEND_SMS);

        } catch (Exception exception) {
            log.error("(Exception) 휴대폰 문자 전송 에러 상세 내용: " + exception.getMessage());
            throw new SmsSendException(ERROR_SEND_SMS);
        }
    }

    private String generateRandomCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private Message setMessage(String phone, String code) {
        Message message = new Message();
        message.setFrom(fromNumber);
        message.setTo(phone);
        message.setText("[윗모닝] 인증번호는 [" + code + "]입니다.");

        return message;
    }

    @Override
    public void verifyCode(String phone, String code, Long userId) {
        User user = userRepository.findById(userId).get();

        String cachedCode = redisTemplate.opsForValue().get(phone);
        log.warn("cachedCode >> " + cachedCode);
        log.warn("code >> " + code);

        if (cachedCode != null && cachedCode.equals(code)) {
            user.setPhone(phone);
            redisTemplate.delete(phone); // 인증 성공 시 코드 삭제
        }
        else {
            throw new VerifyCodeException(UNMATCHED_CODE);
        }
    }

    @Override
    public void setDisturbBanMode(Long userId, Long groupId, Boolean isDisturbBanMode) {
        User user = userRepository.findById(userId).get();
        Group group = groupRepository.findById(groupId).get();

        UserGroup userGroup = userGroupRepository.findByUserAndGroup(user, group);
        if (userGroup != null){
            userGroup.setDisturbBanMode(isDisturbBanMode);
        }
        else {
            throw new BadRequestException(NOT_FOUND_GROUP_ID);
        }
    }

    @Override
    public void setWakeStatus(Long userId, Long groupId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));

        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new EntityNotFoundException("그룹을 찾을 수 없습니다. ID: " + groupId));

        UserGroup userGroup = Optional.ofNullable(userGroupRepository.findByUserAndGroup(user, group))
            .orElseThrow(() -> new EntityNotFoundException("UserGroup을 찾을 수 없습니다."));

        userGroup.setWakeup(true);
    }

    @Override
    public void setPhoneAgree(Long userId, Long groupId, Boolean isAgree) {
        User user = userRepository.findById(userId).get();
        Group group = groupRepository.findById(groupId).get();

        UserGroup userGroup = userGroupRepository.findByUserAndGroup(user, group);

        userGroup.setAgree(isAgree);
    }

    @Override
    public void setSleepStatus() {
        List<UserGroup> userGroupList = userGroupRepository.findAll();

        for (UserGroup userGroup : userGroupList) {
            userGroup.setWakeup(false);
        }
    }
}
