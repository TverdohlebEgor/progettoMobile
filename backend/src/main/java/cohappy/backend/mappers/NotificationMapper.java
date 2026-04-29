package cohappy.backend.mappers;

import cohappy.backend.model.Notification;
import cohappy.backend.model.dto.response.GetNotificationDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class NotificationMapper {

    public GetNotificationDTO notificationToDTO(Notification notification) {
        return new GetNotificationDTO(
                notification.getEventId(),
                notification.getEventType().name(),
                notification.getTitle(),
                notification.getSubtitle(),
                LocalDateTime.ofInstant(notification.getTimestamp(), ZoneId.of("UTC")).toString(),
                notification.getImageBytes(),
                notification.getUserCode()
        );
    }

}
