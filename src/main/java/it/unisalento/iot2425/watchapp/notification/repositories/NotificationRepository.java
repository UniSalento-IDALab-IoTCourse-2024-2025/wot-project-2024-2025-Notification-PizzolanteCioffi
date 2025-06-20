package it.unisalento.iot2425.watchapp.notification.repositories;

import it.unisalento.iot2425.watchapp.notification.domain.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByPatientId(String patientId);


}
