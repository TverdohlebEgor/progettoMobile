package cohappy.backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChatMessageDTO {
    public String message;
    public byte[] messageImmage;
    private String userCode;
    private byte[] userImage;
    public LocalDateTime timestamp;
}
