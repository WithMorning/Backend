package go.alarm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FCMConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(
            new ClassPathResource("firebase-service-account.json").getInputStream());
        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .build();
        return FirebaseApp.initializeApp(options);
    }
    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
