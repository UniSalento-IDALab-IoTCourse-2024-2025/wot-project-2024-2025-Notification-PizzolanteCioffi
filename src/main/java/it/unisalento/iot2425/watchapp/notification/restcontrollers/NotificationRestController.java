package it.unisalento.iot2425.watchapp.notification.restcontrollers;

import it.unisalento.iot2425.watchapp.notification.domain.Notification;
import it.unisalento.iot2425.watchapp.notification.dto.NotificationDTO;
import it.unisalento.iot2425.watchapp.notification.dto.NotificationListDTO;
import it.unisalento.iot2425.watchapp.notification.exceptions.NotificationNotFoundException;
import it.unisalento.iot2425.watchapp.notification.repositories.NotificationRepository;
import it.unisalento.iot2425.watchapp.notification.security.JwtUtilities;
import it.unisalento.iot2425.watchapp.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/api/notification")
public class NotificationRestController {
    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    private JwtUtilities jwtUtilities;

    @Autowired
    private NotificationService notificationService;


    @RequestMapping(value="/delete/{id}",
            method = RequestMethod.DELETE
    )
    public void delete(@PathVariable String id) throws NotificationNotFoundException {
        try{
            notificationRepository.deleteById(id);
        } catch (Exception e){
            throw new NotificationNotFoundException();
        }
    }

    //prendiamo tutte le notifiche
    @RequestMapping(value="/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public NotificationListDTO getAll() {
        List<Notification> notifications = notificationRepository.findAll();
        List<NotificationDTO> list = new ArrayList<>();

        for (Notification notification : notifications) {

            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setId(notification.getId());
            notificationDTO.setAssistantId(notification.getAssistantId());
            notificationDTO.setMessage(notification.getMessage());
            notificationDTO.setPatientId(notification.getPatientId());
            notificationDTO.setDate(notification.getDate());

            list.add(notificationDTO);
        }

        NotificationListDTO notificationListDTO = new NotificationListDTO();
        notificationListDTO.setList(list);

        return notificationListDTO;

    }

    @RequestMapping(value="/hadNotificationsLastTwoDays/{patientId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Boolean> hadNotificationsLastTwoDays(@PathVariable String patientId) {

        boolean result = notificationService.hadNotificationsLastTwoDays(patientId);

        return ResponseEntity.ok(result);
    }


}
