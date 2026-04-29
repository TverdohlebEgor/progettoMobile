package cohappy.backend.controller;

import cohappy.backend.exceptions.NotFoundException;
import cohappy.backend.model.NotificationType;
import cohappy.backend.model.dto.response.GetNotificationDTO;
import cohappy.backend.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cohappy.backend.model.OperationResultMessages.OPERATION_COMPLETED;

@RestController
@RequestMapping("/api/notifications")
@Slf4j
@AllArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{userCode}")
    public ResponseEntity<List<GetNotificationDTO>> getUserNotification(@PathVariable String userCode) {
        try {
            List<GetNotificationDTO> notifications = notificationService.getUserNotification(userCode);
            return ResponseEntity.ok(notifications);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> deleteNotification(@PathVariable String notificationId) {
        try {
            if (notificationService.deleteNotification(notificationId)) {
                return ResponseEntity.ok(OPERATION_COMPLETED);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/clear/{userCode}")
    public ResponseEntity<String> clearNotification(
            @PathVariable String userCode,
            @RequestParam(name = "eventType", required = false) NotificationType eventType
    ) {
        try {
            if (notificationService.clearNotification(userCode, eventType)) {
                return ResponseEntity.ok(OPERATION_COMPLETED);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
