package it.unisalento.iot2425.watchapp.notification.messaging;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;
@Service
public class FirebaseMessagingService {

    public String sendNotification(String fcmToken, String title, String body) {
        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setNotification(AndroidNotification.builder()
                                .setSound("default")
                                .setChannelId("default_channel") // <- molto importante per Android 8+
                                .build())
                        .build())
                .build();

        try {
            return FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return null;
        }
    }
}