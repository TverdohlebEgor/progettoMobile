package cohappy.backend.controller;

import cohappy.backend.exceptions.IllegalInputException;
import cohappy.backend.exceptions.NoPatchException;
import cohappy.backend.exceptions.NotFoundException;
import cohappy.backend.model.dto.request.CreateChoreDTO;
import cohappy.backend.model.dto.request.PatchChoreDTO;
import cohappy.backend.model.dto.response.GetChoreDTO;
import cohappy.backend.service.ChoreService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static cohappy.backend.model.OperationResultMessages.OPERATION_COMPLETED;

@RestController
@RequestMapping("/api/chore")
@Slf4j
@AllArgsConstructor
public class ChoreController {
    private final ChoreService choreService;

    @GetMapping("/{houseCode}/{date}")
    public ResponseEntity<List<GetChoreDTO>> getChore(@PathVariable String houseCode, @PathVariable LocalDate date) {
        try {
            List<GetChoreDTO> result = choreService.getChore(houseCode, date);
            return ResponseEntity.ok(result);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/patch")
    public ResponseEntity<String> patchChore(@RequestBody PatchChoreDTO patchChoreDTO) {
        try {
            choreService.patchChore(patchChoreDTO);
            return ResponseEntity.ok(OPERATION_COMPLETED);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (NoPatchException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createChore(@RequestBody CreateChoreDTO createChoreDTO) {
        try {
            choreService.createChore(createChoreDTO);
            return ResponseEntity.ok(OPERATION_COMPLETED);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
