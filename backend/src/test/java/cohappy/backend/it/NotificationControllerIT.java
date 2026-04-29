package cohappy.backend.it;

import cohappy.backend.model.Notification;
import cohappy.backend.model.NotificationType;
import cohappy.backend.model.UserAccount;
import cohappy.backend.repositories.NotificationRepository;
import cohappy.backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.UUID;

import static cohappy.backend.model.NotificationType.CHAT;
import static cohappy.backend.model.NotificationType.CHORE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class NotificationControllerIT extends BaseIT {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String path(String userCode) {
        return "/api/notifications/" + userCode;
    }

    // --- GET /{userCode} ---

    @Test
    void shouldGetUserNotifications() throws Exception {
        saveDefaultUser();
        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        saveNotification("NOT-001", CHAT, "Messaggio 1", time.minusMinutes(10),"USR-999");
        saveNotification("NOT-002", CHAT, "Messaggio 2", time,"USR-999");

        mockMvc.perform(get(path("USR-999"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].subtitle").value("Messaggio 2"))
                .andExpect(jsonPath("$[1].subtitle").value("Messaggio 1"))
                .andExpect(jsonPath("$[0].timestamp").value(time.toString()));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get(path("NON_EXISTENT_USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoNotifications() throws Exception {
        saveDefaultUser();

        mockMvc.perform(get(path("USR-999"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
    // --- DELETE /{notificationId} ---

    @Test
    void shouldDeleteSpecificNotification() throws Exception {
        saveNotification("NOT-001", CHAT, "Target Message", LocalDateTime.now(),"USR-999");

        mockMvc.perform(delete("/api/notifications/NOT-001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(notificationRepository.count()).isZero();
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentNotification() throws Exception {
        mockMvc.perform(delete("/api/notifications/NON-EXISTENT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // --- DELETE /clear/{userCode} ---

    @Test
    void shouldClearAllNotificationsForUser() throws Exception {
        saveDefaultUser();
        LocalDateTime time = LocalDateTime.now();
        saveNotification("NOT-001", CHAT, "Msg 1",time,"USR-999");
        saveNotification("NOT-002", CHAT, "Msg 2",time,"USR-999");

        mockMvc.perform(delete("/api/notifications/clear/USR-999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(notificationRepository.findByUserCode("USR-999").size()).isZero();
    }

    @Test
    void shouldClearOnlySpecificEventType() throws Exception {
        saveDefaultUser();
        LocalDateTime time = LocalDateTime.now();
        saveNotification("NOT-001", CHAT, "Chat Msg",time,"USR-999");
        saveNotification("NOT-002", CHORE, "System Msg",time,"USR-999");

        // Clear only CHAT notifications
        mockMvc.perform(delete("/api/notifications/clear/USR-999")
                        .param("eventType", "CHAT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify CHAT is gone, but SYSTEM remains
        assertThat(notificationRepository.count()).isEqualTo(1);
        assertThat(notificationRepository.findAll().getFirst().getEventType()).isEqualTo(CHORE);
    }

    @Test
    void shouldReturnNotFoundWhenClearingNotificationsForNonExistentUser() throws Exception {
        mockMvc.perform(delete("/api/notifications/clear/UNKNOWN_USER")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private void saveDefaultUser() {
        UserAccount user = new UserAccount();
        user.setUserCode("USR-999");
        user.setEmail("test@cohappy.it");
        userRepository.save(user);
    }

    private void saveNotification(String code, NotificationType type, String message, LocalDateTime timestamp,String userCode) {
        Notification notification = new Notification(
                code,
                type,
                "title",
                message,
                timestamp.toInstant(ZoneOffset.UTC),
                null,
                userCode
        );
        notificationRepository.save(notification);
    }
}