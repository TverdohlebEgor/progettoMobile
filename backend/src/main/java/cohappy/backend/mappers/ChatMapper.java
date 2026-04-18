package cohappy.backend.mappers;

import cohappy.backend.model.ChatMessage;
import cohappy.backend.model.dto.response.ChatMessageDTO;

public class ChatMapper {
    public ChatMessageDTO messageToDTO(ChatMessage chatMessage){
        return new ChatMessageDTO(
            chatMessage.getMessage(),
            chatMessage.getMessageImmage(),
            chatMessage.getUserCode(),
            chatMessage.getUserImage(),
            chatMessage.getTimestamp()
        );
    }
}
