package cohappy.backend.model;

import lombok.Data;

import java.util.List;

@Data
public class Chat {
    public List<UserAccount> participating;
    public String name;
    public List<ChatMessage> messages;
}
