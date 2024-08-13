package go.alarm.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class FCMConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // 프로젝트 파일과 동일한 디렉토리에 있는 firebase_service_key.json 파일 경로
        String filePath = "./firebase_service_key.json";

        // FileSystemResource를 사용하여 파일 시스템에서 리소스를 로드
        Resource resource = new FileSystemResource(filePath);
        InputStream serviceAccount = resource.getInputStream();

        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
