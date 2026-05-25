package cohappy.backend.controller;

import cohappy.backend.exceptions.IllegalInputException;
import cohappy.backend.exceptions.NoPatchException;
import cohappy.backend.exceptions.NotFoundException;
import cohappy.backend.mappers.DebtMapper;
import cohappy.backend.model.dto.request.CreateDebtDTO;
import cohappy.backend.model.dto.request.PatchDebtPaidDTO;
import cohappy.backend.model.dto.response.DebtDTO;
import cohappy.backend.repositories.DebtRepository;
import cohappy.backend.service.DebtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cohappy.backend.model.OperationResultMessages.OPERATION_COMPLETED;

@RestController
@RequestMapping("/api/debts")
@Slf4j
public class DebtController {
    private final DebtService debtService;
    private final DebtRepository debtRepository;
    private final DebtMapper debtMapper = new DebtMapper();

    public DebtController(DebtService debtService, DebtRepository debtRepository) {
        this.debtService = debtService;
        this.debtRepository = debtRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createDebtMoney(@RequestBody CreateDebtDTO createDebtDTO) {
        try {
            debtService.createDebtMoney(createDebtDTO);
            return ResponseEntity.ok(OPERATION_COMPLETED);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PatchMapping("/patch/paid")
    public ResponseEntity<String> patchDebtPaid(@RequestBody PatchDebtPaidDTO patchDebtPaidDTO) {
        try {
            debtService.patchDebtPaid(patchDebtPaidDTO);
            return ResponseEntity.ok(OPERATION_COMPLETED);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoPatchException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{debtId}")
    public ResponseEntity<String> deleteDebt(@PathVariable String debtId) {
        try {
            debtService.deleteDebt(debtId);
            return ResponseEntity.ok(OPERATION_COMPLETED);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<DebtDTO>> getAllDebts() {
        try {
            return ResponseEntity.ok(
                    debtRepository.findAll().stream()
                            .map(debtMapper::debtToDTO)
                            .toList()
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{userCode}/total")
    public ResponseEntity<Float> getUserTotalDebt(@PathVariable String userCode) {
        try {
            return ResponseEntity.ok(
                    (float) debtRepository.findAll().stream()
                            .filter(d -> d.getDebtorsCode().containsKey(userCode))
                            .mapToDouble(d -> d.getAmount())
                            .sum()
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/credits/{userCode}/total")
    public ResponseEntity<Float> getUserTotalCredits(@PathVariable String userCode) {
        try {
            return ResponseEntity.ok(
                    (float) debtRepository.findAll().stream()
                            .filter(d -> d.getCreditorUserCode().equals(userCode))
                            .mapToDouble(d -> d.getAmount())
                            .sum()
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
