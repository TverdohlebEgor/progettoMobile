package cohappy.backend.repositories;

import cohappy.backend.model.Notification;
import cohappy.backend.model.NotificationType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserCode(String userCode);

    long deleteByEventId(String houseCode);

    long deleteByUserCode(String userCode);

    long deleteByUserCodeAndEventType(String userCode, NotificationType eventType);
}
