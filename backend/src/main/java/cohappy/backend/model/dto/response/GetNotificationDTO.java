package cohappy.backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetNotificationDTO {
    private String eventId;
    private String eventType;
    private String title;
    private String subtitle;
    private String timestamp;
    private byte[] imageBytes;
    private String userCode;
}
