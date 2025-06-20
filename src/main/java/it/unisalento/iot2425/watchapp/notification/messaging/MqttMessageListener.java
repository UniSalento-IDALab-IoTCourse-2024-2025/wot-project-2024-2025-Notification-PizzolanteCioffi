package it.unisalento.iot2425.watchapp.notification.messaging;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;

import it.unisalento.iot2425.watchapp.notification.domain.Notification;
import it.unisalento.iot2425.watchapp.notification.repositories.NotificationRepository;
import it.unisalento.iot2425.watchapp.notification.restcontrollers.NotificationRestController;
import it.unisalento.iot2425.watchapp.notification.service.NotificationService;
import jakarta.mail.internet.MimeMessage;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

import static it.unisalento.iot2425.watchapp.notification.messaging.MqttSubscriberConfig.TOPICDATA;
import static it.unisalento.iot2425.watchapp.notification.messaging.MqttSubscriberConfig.TOPICREGISTRATION;
import static it.unisalento.iot2425.watchapp.notification.security.SecurityConstants.JWT_ISSUER;

@Component
public class MqttMessageListener {

    @Autowired
     NotificationRepository notificationRepository;

    @Autowired
     NotificationService notificationService;

    @Autowired
     JavaMailSender mailSender;

    public void handleMessage(String topic, Map<String, Object> message) {
        System.out.println("Messaggio gestito da handler: " + topic + " → " + message);

        if(topic.equals(TOPICDATA)){
            //manda notifica

            Notification notification = new Notification();
            notification.setMessage((String) message.get("message"));
            notification.setPatientId((String) message.get("patientId"));
            notification.setAssistantId((String) message.get("assistantId"));
            notification.setDate((String )message.get("date"));

            notificationRepository.save(notification);

            //prendiamo l'fcm token
            String uri ="http://localhost:8080/api/users/patient/" + notification.getPatientId();

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + JWT_ISSUER);
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference<Map<String, Object>>() {});

            //madiamo notifica al paziente
            String fcmToken=(String) response.getBody().get("fcmToken");
            try{
                Message fcmMessage = Message.builder()
                        .setToken(fcmToken)
                        .setNotification(
                                com.google.firebase.messaging.Notification.builder()
                                        .setTitle("Notifica")
                                        .setBody(notification.getMessage())
                                        .build()
                        )
                        .build();
                String responseFcm= FirebaseMessaging.getInstance().send(fcmMessage);
                System.out.println("FCM message sent successfully: "+ responseFcm);
            } catch (Exception e){
                e.printStackTrace();
                System.out.println("Errore durante l'invio della notifica FCM: " + e.getMessage());
            }

            boolean shoudNotifyAssistant = notificationService.hadNotificationsLastTwoDays(notification.getPatientId());
            if(shoudNotifyAssistant){
                //mandiamo notifica all'assistente
                 uri ="http://localhost:8080/api/users/assistant/" + notification.getAssistantId();

                 restTemplate = new RestTemplate();
                 headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + JWT_ISSUER);
                entity = new HttpEntity<>("parameters", headers);
                 response = restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference<Map<String, Object>>() {});

                 fcmToken=(String) response.getBody().get("fcmToken");
                try{
                    Message fcmMessage = Message.builder()
                            .setToken(fcmToken)
                            .setNotification(
                                    com.google.firebase.messaging.Notification.builder()
                                            .setTitle("Notifica")
                                            .setBody(notification.getMessage())
                                            .build()
                            )
                            .build();
                    String responseFcm= FirebaseMessaging.getInstance().send(fcmMessage);
                    System.out.println("FCM message sent successfully: "+ responseFcm);
                } catch (Exception e){
                    e.printStackTrace();
                    System.out.println("Errore durante l'invio della notifica FCM: " + e.getMessage());
                }

            }

        } else if (topic.equals(TOPICREGISTRATION)){

            //manda email.
            try {

                MimeMessage email = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(email, true, "UTF-8");
                helper.setTo((String) message.get("receiverEmail"));
                helper.setSubject((String) message.get("subject"));
                helper.setText((String) message.get("message"), true);

                mailSender.send(email);

            } catch (Exception e){
                e.printStackTrace();  // Questo ti darà più dettagli sull'errore
                System.out.println("Errore durante l'invio dell'email: " + e.getMessage());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        }
    }
}
