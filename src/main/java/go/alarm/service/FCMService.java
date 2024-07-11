package go.alarm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FCMService {

    private final FirebaseMessaging firebaseMessaging;

    @Autowired
    public FCMService(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    public void sendNotification(String token, String title, String body) throws FirebaseMessagingException {
        Message message = Message.builder()
            .setToken(token)
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .build();

        firebaseMessaging.send(message);
    }
}