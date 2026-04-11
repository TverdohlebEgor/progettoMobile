package cohappy.backend.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessage {
    public String message;
    public byte[] immage;
    public UserAccount from;
    public LocalDateTime timestamp;
}
