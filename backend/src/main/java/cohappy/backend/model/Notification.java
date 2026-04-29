package cohappy.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Data
@AllArgsConstructor
public class Notification {
    @Id
    private String eventId;
    private NotificationType eventType;
    private String title;
    private String subtitle;
    private Instant timestamp;
    private byte[] imageBytes;
    private String userCode;
}
