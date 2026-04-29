package cohappy.backend.service;

import cohappy.backend.exceptions.NotFoundException;
import cohappy.backend.mappers.NotificationMapper;
import cohappy.backend.model.Notification;
import cohappy.backend.model.NotificationType;
import cohappy.backend.model.dto.response.GetNotificationDTO;
import cohappy.backend.repositories.NotificationRepository;
import cohappy.backend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static cohappy.backend.model.OperationResultMessages.USER_NOT_FOUND;

@Service
@AllArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    private final NotificationMapper mapper = new NotificationMapper();

    public void createNotification(NotificationType type, String title, String subTitle, byte[] immage, String userCode) {
        notificationRepository.save(new Notification(
                UUID.randomUUID().toString(),
                type,
                title,
                subTitle,
                Instant.now(),
                immage,
                userCode
        ));
    }

    public List<GetNotificationDTO> getUserNotification(String userCode) {
        userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(userCode)));

        List<Notification> notifications = notificationRepository.findByUserCode(userCode);

        return notifications.stream()
                .sorted(Comparator.comparing(Notification::getTimestamp).reversed())
                .map(mapper::notificationToDTO)
                .toList();
    }

    public boolean deleteNotification(String notificationId) {
        return notificationRepository.deleteByEventId(notificationId) > 0;
    }

    public boolean clearNotification(String userCode, NotificationType eventType) {
    if (eventType != null) {
        return notificationRepository.deleteByUserCodeAndEventType(userCode, eventType) > 0;
    } else{
        return notificationRepository.deleteByUserCode(userCode) > 0;
    }
}
}
