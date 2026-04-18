package cohappy.backend.controller;

import cohappy.backend.exceptions.IllegalInputException;
import cohappy.backend.exceptions.NoPatchException;
import cohappy.backend.exceptions.NotFoundException;
import cohappy.backend.model.dto.request.*;
import cohappy.backend.model.dto.response.ChatMessageDTO;
import cohappy.backend.model.dto.response.GetChatDTO;
import cohappy.backend.model.dto.response.UserChatDTO;
import cohappy.backend.service.ChatService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cohappy.backend.model.OperationResultMessages.OPERATION_COMPLETED;

@RestController
@RequestMapping("/api/chat")
@Slf4j
@AllArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/user/{userCode}")
    public ResponseEntity<List<UserChatDTO>> getUserChats(@PathVariable String userCode) {
        try {
            List<UserChatDTO> chats = chatService.getUserChats(userCode);
            return ResponseEntity.ok(chats);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{chatCode}")
    public ResponseEntity<GetChatDTO> getChat(@PathVariable String chatCode) {
        try {
            return ResponseEntity.ok(chatService.getChat(chatCode));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/messages/{chatCode}")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(@PathVariable String chatCode, GetMessagesDTO getMessagesDTO) {
        try {
            List<ChatMessageDTO> response = chatService.getMessages(chatCode, getMessagesDTO);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createChat(@RequestBody CreateChatDTO createChatDTO) {
        try {
            String chatCode = chatService.createChat(createChatDTO);
            return ResponseEntity.ok(chatCode);
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PatchMapping("/patch")
    public ResponseEntity<String> patchChat(@RequestBody PatchChatDTO patchChatDTO) {
        try {
            chatService.patchChat(patchChatDTO);
            return ResponseEntity.ok(OPERATION_COMPLETED);
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (NoPatchException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PatchMapping("/patch/user")
    public ResponseEntity<String> patchChatUsers(@RequestBody PatchChatUsersDTO patchChatUsersDTO) {
        try {
            chatService.patchChatUsers(patchChatUsersDTO);
            return ResponseEntity.ok(OPERATION_COMPLETED);
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/message/add")
    public ResponseEntity<String> addMessage(@RequestBody AddMessageDTO addMessageDTO) {
        try {
            chatService.addMessage(addMessageDTO);
            return ResponseEntity.ok(OPERATION_COMPLETED);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
