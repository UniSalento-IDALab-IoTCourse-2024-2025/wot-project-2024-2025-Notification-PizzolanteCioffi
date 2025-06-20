package it.unisalento.iot2425.watchapp.notification.service;

import it.unisalento.iot2425.watchapp.notification.domain.Notification;
import it.unisalento.iot2425.watchapp.notification.repositories.NotificationRepository;
import it.unisalento.iot2425.watchapp.notification.restcontrollers.NotificationRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    public Boolean hadNotificationsLastTwoDays(String patientId) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate twoDaysAgo = today.minusDays(2);

        List<Notification> notifications = notificationRepository.findByPatientId(patientId);

        boolean foundYesterday = notifications.stream()
                .anyMatch(n -> LocalDate.parse(n.getDate()).equals(yesterday));
        boolean foundTwoDaysAgo = notifications.stream()
                .anyMatch(n -> LocalDate.parse(n.getDate()).equals(twoDaysAgo));

        boolean result = foundYesterday && foundTwoDaysAgo;

        return result;
    }
}
