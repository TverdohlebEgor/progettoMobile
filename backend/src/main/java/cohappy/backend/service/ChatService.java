package cohappy.backend.service;

import cohappy.backend.exceptions.IllegalInputException;
import cohappy.backend.exceptions.NoPatchException;
import cohappy.backend.exceptions.NotFoundException;
import cohappy.backend.mappers.ChatMapper;
import cohappy.backend.model.Chat;
import cohappy.backend.model.ChatMessage;
import cohappy.backend.model.UserAccount;
import cohappy.backend.model.dto.request.*;
import cohappy.backend.model.dto.response.ChatMessageDTO;
import cohappy.backend.model.dto.response.GetChatDTO;
import cohappy.backend.model.dto.response.UserChatDTO;
import cohappy.backend.repositories.ChatRepository;
import cohappy.backend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static cohappy.backend.model.OperationResultMessages.*;

@Service
@AllArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatMapper mapper = new ChatMapper();

    public List<UserChatDTO> getUserChats(String userCode) {
        List<UserChatDTO> userChats = new ArrayList<>();

        userRepository.findByUserCode(userCode).orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND.formatted(userCode))
        );

        for (Chat chat : chatRepository.findByParticipatingContains(userCode)) {
            userChats.add(new UserChatDTO(
                            chat.getChatCode(),
                            chat.getParticipating(),
                            chat.getName(),
                            chat.getImmage()
                    )
            );
        }

        return userChats;
    }

    public GetChatDTO getChat(String chatCode) {
        Chat chat = chatRepository.findByChatCode(chatCode).orElseThrow(
                () -> new NotFoundException(CHAT_NOT_FOUND.formatted(chatCode))
        );

        return new GetChatDTO(
               chat.getChatCode(),
                chat.getParticipating(),
                chat.getName(),
                chat.getImmage()
        );
    }

    private final MongoTemplate mongoTemplate;

    public List<ChatMessageDTO> getMessages(String chatCode, GetMessagesDTO dto) {
        chatRepository.findByChatCode(chatCode).orElseThrow(
                () -> new NotFoundException(CHAT_NOT_FOUND.formatted(chatCode))
        );

        int start = (dto.getStartProgressive() == null) ? 0 : dto.getStartProgressive();
        int end = (dto.getEndProgressive() == null) ? Integer.MAX_VALUE : dto.getEndProgressive();
        int limit = Math.max(0, end - start);
        Query query = Query.query(Criteria.where("_id").is(chatCode));
        query.fields().slice("messages", start, limit);

        Chat chat = mongoTemplate.findOne(query, Chat.class);

        return (chat != null && chat.getMessages() != null)
                ? chat.getMessages().stream().map(mapper::messageToDTO).toList()
                : Collections.emptyList();
    }

    public String createChat(CreateChatDTO createChatDTO){
        validateInput(createChatDTO,"createChatDTO");
        validateInput(createChatDTO.getName(),"chatName");
        validateInput(createChatDTO.getParticipating(),"Participating");
        if(createChatDTO.getParticipating().isEmpty()){
            throw new IllegalInputException("Chat participators can't be 0");
        }

        Chat newChat = new Chat(
                UUID.randomUUID().toString(),
                createChatDTO.getParticipating(),
                createChatDTO.getName(),
                createChatDTO.getImmage(),
                new ArrayList<>()
        );

        return newChat.getChatCode();
    }

    public void patchChat(PatchChatDTO patchChatDTO){
        boolean changedSomething = false;
        validateInput(patchChatDTO,"patchChatDTO");

        Chat chat = chatRepository.findByChatCode(patchChatDTO.getChatCode()).orElseThrow(
                () -> new NotFoundException(CHAT_NOT_FOUND.formatted(patchChatDTO.getChatCode()))
        );

        if(!Arrays.equals(patchChatDTO.getImmage(),chat.getImmage())){
            chat.setImmage(patchChatDTO.getImmage());
            changedSomething = true;
        }

        if(!patchChatDTO.getName().equals(chat.getName())){
            chat.setName(patchChatDTO.getName());
            changedSomething = true;
        }

        if(!changedSomething){
            throw new NoPatchException(NO_PATCH.formatted(patchChatDTO.getChatCode()));
        }

        chatRepository.save(chat);
    }

    public void patchChatUsers(PatchChatUsersDTO patchChatUsersDTO){
        validateInput(patchChatUsersDTO,"patchChatUsersDTO");

        Chat chat = chatRepository.findByChatCode(patchChatUsersDTO.getChatCode()).orElseThrow(
                () -> new NotFoundException(CHAT_NOT_FOUND.formatted(patchChatUsersDTO.getChatCode()))
        );

        for(String userCode : patchChatUsersDTO.getUsersCode()){
            userRepository.findByUserCode(userCode).orElseThrow(
                    () -> new NotFoundException(USER_NOT_FOUND.formatted(userCode))
            );

            if(patchChatUsersDTO.getOperation() == PatchChatUsersOperationEnum.ADD){
                chat.getParticipating().add(userCode);
            } else {
                if(chat.getParticipating().size() <= 1){
                   throw new IllegalInputException("Chat %s has only 1 member".formatted(userCode));
                }
                if(!chat.getParticipating().contains(userCode)){
                    throw new IllegalInputException("User %s is not in the chat %s".formatted(userCode,chat.getChatCode()));
                }

                chat.getParticipating().remove(userCode);
            }
        }

        chatRepository.save(chat);
    }

    public void addMessage(AddMessageDTO addMessageDTO){
        validateInput(addMessageDTO,"addMessageDTO");
        validateInput(addMessageDTO.getMessage(),"message");
        validateInput(addMessageDTO.getUserCode(),"userCode");

        Chat chat = chatRepository.findByChatCode(addMessageDTO.getChatCode()).orElseThrow(
                () -> new NotFoundException(CHAT_NOT_FOUND.formatted(addMessageDTO.getChatCode()))
        );

        UserAccount userAccount = userRepository.findByUserCode(addMessageDTO.getUserCode()).orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND.formatted(addMessageDTO.getUserCode()))
        );

        ChatMessage newMessage = new ChatMessage(
           addMessageDTO.getMessage(),
           addMessageDTO.getMessageImmage(),
           userAccount.getUserCode(),
           userAccount.getImages().getFirst(),
           LocalDateTime.now()
        );

        chat.getMessages().add(newMessage);
        chatRepository.save(chat);
    }

    private void validateInput(Object value, String name){
        if(value == null){
            throw new IllegalInputException(INVALID_INPUT.formatted(name));
        }
    }
}
