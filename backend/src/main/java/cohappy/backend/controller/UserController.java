package cohappy.backend.controller;

import cohappy.backend.exceptions.IllegalInputException;
import cohappy.backend.exceptions.NoPatchException;
import cohappy.backend.exceptions.NotFoundException;
import cohappy.backend.model.dto.request.LoginDTO;
import cohappy.backend.model.dto.request.PatchUserDTO;
import cohappy.backend.model.dto.request.RegisterDTO;
import cohappy.backend.model.dto.response.UserAccountDTO;
import cohappy.backend.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static cohappy.backend.model.OperationResultMessages.OPERATION_COMPLETED;

@RestController
@RequestMapping("/api/user")
@Slf4j
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile/{userCode}")
    public ResponseEntity<UserAccountDTO> getUserProfile(@PathVariable String userCode) {
        try {
            UserAccountDTO account = userService.getUserProfile(userCode);
            return ResponseEntity.ok(account);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete/{userCode}")
    public ResponseEntity<String> deleteProfile(@PathVariable String userCode) {
        try {
            if(userService.deleteProfile(userCode)){
                return ResponseEntity.ok(OPERATION_COMPLETED);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PatchMapping("/patch")
    public ResponseEntity<String> patchProfile(@RequestBody PatchUserDTO patchUserDTO) {
        try {
            userService.patchProfile(patchUserDTO);
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

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {
        try {
            String userCode = userService.login(loginDTO);
            return ResponseEntity.ok(userCode);
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO){
        try {
            String userCode = userService.register(registerDTO);
            return ResponseEntity.ok(userCode);
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
