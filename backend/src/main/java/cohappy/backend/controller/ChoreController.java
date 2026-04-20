package cohappy.backend.controller;

import cohappy.backend.exceptions.NotFoundException;
import cohappy.backend.exceptions.NotImplementedException;
import cohappy.backend.model.dto.response.UserAccountDTO;
import cohappy.backend.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chore")
@Slf4j
@AllArgsConstructor
public class ChoreController {
    private final UserService userService;

    @GetMapping("/{choreCode}")
    public ResponseEntity<UserAccountDTO> getChore(@PathVariable String choreCode) {
        try {
            throw new NotImplementedException("");
            //UserAccountDTO account = userService.getUserProfile(userCode);
            //return ResponseEntity.ok(account);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
