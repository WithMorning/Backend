package go.alarm.fcm.config;


import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Configuration;

/**
 * FCM을 사용하면서도 iOS 디바이스에 특화된 APNS(Apple Push Notification Service) 설정을 적용하기 위한 Config 파일
 * */
@Configuration
public class ApnsConfigBuilder {

    /**
     * 기본 APNS 설정을 생성합니다.
     * @param sound 알림음 파일 이름
     * @return ApnsConfig 객체
     */
    public static ApnsConfig buildApnsConfig(String sound) {
        Map<String, Object> apnsPayload = new HashMap<>();
        Map<String, Object> aps = new HashMap<>(); // 'aps'는 APNS의 핵심 구성 요소
        aps.put("sound", sound);
        apnsPayload.put("aps", aps);

        return ApnsConfig.builder()
            .setAps(Aps.builder().setSound(sound).build())
            .putAllCustomData(apnsPayload)
            .build();
    }
}