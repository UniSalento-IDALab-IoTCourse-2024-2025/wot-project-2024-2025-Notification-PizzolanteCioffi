package it.unisalento.iot2425.watchapp.notification.dto;

import java.util.List;

public class NotificationListDTO {
    private List<NotificationDTO> list;

    public List<NotificationDTO> getList() {
        return list;
    }

    public void setList(List<NotificationDTO> list) {
        this.list = list;
    }
}
