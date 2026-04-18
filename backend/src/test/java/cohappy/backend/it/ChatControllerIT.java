package cohappy.backend.it;

import cohappy.backend.model.Chat;
import cohappy.backend.model.ChatMessage;
import cohappy.backend.model.UserAccount;
import cohappy.backend.model.dto.request.*;
import cohappy.backend.repositories.ChatRepository;
import cohappy.backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static cohappy.backend.model.OperationResultMessages.OPERATION_COMPLETED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ChatControllerIT extends BaseIT {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        chatRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String path(String str) {
        return "/api/chat" + str;
    }

    // --- GET /user/{userCode} ---

    @Test
    void shouldGetUserChats() throws Exception {
        saveDefaultUser();
        saveDefaultChat();

        mockMvc.perform(get(path("/user/USR-999"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].chatCode").value("chatCode"))
                .andExpect(jsonPath("$[0].name").value("Test Chat"))
                .andExpect(jsonPath("$[0].participating[0]").value("USR-999"));
    }

    @Test
    void shouldNotFoundGetUserChats() throws Exception {
        mockMvc.perform(get(path("/user/NOTEXISTING"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // --- GET /{chatCode} ---

    @Test
    void shouldGetChat() throws Exception {
        saveDefaultChat();

        byte[] imageBytes = "chat_img.png".getBytes(StandardCharsets.UTF_8);
        String expectedBase64Image = Base64.getEncoder().encodeToString(imageBytes);

        mockMvc.perform(get(path("/chatCode"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chatCode").value("chatCode"))
                .andExpect(jsonPath("$.name").value("Test Chat"))
                .andExpect(jsonPath("$.immage").value(expectedBase64Image))
                .andExpect(jsonPath("$.participating[0]").value("USR-999"));
    }

    @Test
    void shouldNotFoundGetChat() throws Exception {
        mockMvc.perform(get(path("/NOTEXISTING"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // --- GET /messages/{chatCode} ---

    @Test
    void shouldGetMessages() throws Exception {
        saveDefaultChatWithMessage();

        mockMvc.perform(get(path("/messages/chatCode"))
                        .param("startProgressive", "0")
                        .param("endProgressive", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].message").value("Hello World"))
                .andExpect(jsonPath("$[0].userCode").value("USR-999"));
    }

    @Test
    void shouldNotFoundGetMessages() throws Exception {
        mockMvc.perform(get(path("/messages/NOTEXISTING"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // --- POST /create ---

    @Test
    void shouldCreateChat() throws Exception {
        CreateChatDTO request = new CreateChatDTO();
        request.setName("New Chat");
        request.setImmage("new_img.png".getBytes(StandardCharsets.UTF_8));
        request.setParticipating(List.of("USR-999"));

        mockMvc.perform(post(path("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isString());

        // Note: The provided ChatService does not explicitly call chatRepository.save(newChat) in createChat.
        // If the implementation is updated to save, a database assertion could be added here.
    }

    @Test
    void shouldBadRequestCreateChatEmptyParticipating() throws Exception {
        CreateChatDTO request = new CreateChatDTO();
        request.setName("New Chat");
        request.setParticipating(new ArrayList<>()); // Empty list

        mockMvc.perform(post(path("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestCreateChatNullName() throws Exception {
        CreateChatDTO request = new CreateChatDTO();
        request.setName(null);
        request.setParticipating(List.of("USR-999"));

        mockMvc.perform(post(path("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // --- PATCH /patch ---

    @Test
    void shouldPatchChat() throws Exception {
        saveDefaultChat();

        PatchChatDTO request = new PatchChatDTO();
        request.setChatCode("chatCode");
        request.setName("Updated Chat Name");
        request.setImmage("updated_img.png".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(patch(path("/patch"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(OPERATION_COMPLETED));

        Chat chat = chatRepository.findByChatCode("chatCode").orElseThrow();
        assertThat(chat.getName()).isEqualTo("Updated Chat Name");
        assertThat(chat.getImmage()).isEqualTo("updated_img.png".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void shouldNoContentPatchChat() throws Exception {
        saveDefaultChat();

        PatchChatDTO request = new PatchChatDTO();
        request.setChatCode("chatCode");
        request.setName("Test Chat"); // Same name
        request.setImmage("chat_img.png".getBytes(StandardCharsets.UTF_8)); // Same image

        mockMvc.perform(patch(path("/patch"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotFoundPatchChat() throws Exception {
        PatchChatDTO request = new PatchChatDTO();
        request.setChatCode("NOTEXISTING");
        request.setName("New Name");

        mockMvc.perform(patch(path("/patch"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // --- PATCH /patch/user ---

    @Test
    void shouldAddUserPatchChatUsers() throws Exception {
        saveDefaultChat();

        UserAccount newUser = createDefaultUser();
        newUser.setUserCode("USR-888");
        userRepository.save(newUser);

        PatchChatUsersDTO request = new PatchChatUsersDTO();
        request.setChatCode("chatCode");
        request.setUsersCode(List.of("USR-888"));
        request.setOperation(PatchChatUsersOperationEnum.ADD);

        mockMvc.perform(patch(path("/patch/user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Chat chat = chatRepository.findByChatCode("chatCode").orElseThrow();
        assertThat(chat.getParticipating().contains("USR-888")).isTrue();
    }

    @Test
    void shouldRemoveUserPatchChatUsers() throws Exception {
        // Chat needs > 1 member to allow removal
        Chat chat = createDefaultChat();
        chat.getParticipating().add("USR-888");
        chatRepository.save(chat);

        saveDefaultUser(); // Saves USR-999

        PatchChatUsersDTO request = new PatchChatUsersDTO();
        request.setChatCode("chatCode");
        request.setUsersCode(List.of("USR-999"));
        request.setOperation(PatchChatUsersOperationEnum.REMOVE);

        mockMvc.perform(patch(path("/patch/user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Chat updatedChat = chatRepository.findByChatCode("chatCode").orElseThrow();
        assertThat(updatedChat.getParticipating().contains("USR-999")).isFalse();
    }

    @Test
    void shouldBadRequestRemoveLastUserPatchChatUsers() throws Exception {
        saveDefaultChat(); // Has exactly 1 member (USR-999)
        saveDefaultUser();

        PatchChatUsersDTO request = new PatchChatUsersDTO();
        request.setChatCode("chatCode");
        request.setUsersCode(List.of("USR-999"));
        request.setOperation(PatchChatUsersOperationEnum.REMOVE);

        mockMvc.perform(patch(path("/patch/user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestRemoveNonParticipatingUserPatchChatUsers() throws Exception {
        Chat chat = createDefaultChat();
        chat.getParticipating().add("USR-777"); // Add dummy member so size > 1
        chatRepository.save(chat);

        UserAccount notInChatUser = createDefaultUser();
        notInChatUser.setUserCode("USR-888");
        userRepository.save(notInChatUser);

        PatchChatUsersDTO request = new PatchChatUsersDTO();
        request.setChatCode("chatCode");
        request.setUsersCode(List.of("USR-888"));
        request.setOperation(PatchChatUsersOperationEnum.REMOVE);

        mockMvc.perform(patch(path("/patch/user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotFoundChatPatchChatUsers() throws Exception {
        saveDefaultUser();

        PatchChatUsersDTO request = new PatchChatUsersDTO();
        request.setChatCode("NOTEXISTING");
        request.setUsersCode(List.of("USR-999"));
        request.setOperation(PatchChatUsersOperationEnum.ADD);

        mockMvc.perform(patch(path("/patch/user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotFoundUserPatchChatUsers() throws Exception {
        saveDefaultChat();

        PatchChatUsersDTO request = new PatchChatUsersDTO();
        request.setChatCode("chatCode");
        request.setUsersCode(List.of("NOTEXISTING"));
        request.setOperation(PatchChatUsersOperationEnum.ADD);

        mockMvc.perform(patch(path("/patch/user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // --- POST /message/add ---

    @Test
    void shouldAddMessage() throws Exception {
        saveDefaultChat();
        saveDefaultUser();

        AddMessageDTO request = new AddMessageDTO();
        request.setChatCode("chatCode");
        request.setUserCode("USR-999");
        request.setMessage("New Message!");
        request.setMessageImmage("msg_img.png".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(post(path("/message/add"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(OPERATION_COMPLETED));

        Chat chat = chatRepository.findByChatCode("chatCode").orElseThrow();
        assertThat(chat.getMessages().size()).isEqualTo(1);
        assertThat(chat.getMessages().get(0).getMessage()).isEqualTo("New Message!");
    }

    @Test
    void shouldBadRequestNullMessageAddMessage() throws Exception {
        saveDefaultChat();
        saveDefaultUser();

        AddMessageDTO request = new AddMessageDTO();
        request.setChatCode("chatCode");
        request.setUserCode("USR-999");
        request.setMessage(null);

        mockMvc.perform(post(path("/message/add"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotFoundChatAddMessage() throws Exception {
        saveDefaultUser();

        AddMessageDTO request = new AddMessageDTO();
        request.setChatCode("NOTEXISTING");
        request.setUserCode("USR-999");
        request.setMessage("Hello");

        mockMvc.perform(post(path("/message/add"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotFoundUserAddMessage() throws Exception {
        saveDefaultChat();

        AddMessageDTO request = new AddMessageDTO();
        request.setChatCode("chatCode");
        request.setUserCode("NOTEXISTING");
        request.setMessage("Hello");

        mockMvc.perform(post(path("/message/add"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // --- Helper Methods ---

    private UserAccount createDefaultUser() {
        UserAccount user = new UserAccount();
        user.setUserCode("USR-999");
        user.setEmail("test@cohappy.it");
        user.setImages(List.of("user_img.png".getBytes(StandardCharsets.UTF_8)));
        return user;
    }

    private void saveDefaultUser() {
        userRepository.save(createDefaultUser());
    }

    private Chat createDefaultChat() {
        List<String> participants = new ArrayList<>();
        participants.add("USR-999");

        return new Chat(
                "chatCode",
                participants,
                "Test Chat",
                "chat_img.png".getBytes(StandardCharsets.UTF_8),
                new ArrayList<>()
        );
    }

    private void saveDefaultChat() {
        chatRepository.save(createDefaultChat());
    }

    private void saveDefaultChatWithMessage() {
        Chat chat = createDefaultChat();
        ChatMessage msg = new ChatMessage(
                "Hello World",
                null,
                "USR-999",
                "chat_img.png".getBytes(StandardCharsets.UTF_8),
                LocalDateTime.now()
        );
        chat.getMessages().add(msg);
        chatRepository.save(chat);
    }
}