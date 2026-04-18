package cohappy.backend.controller;

import cohappy.backend.exceptions.*;
import cohappy.backend.model.dto.request.CreateDebtDTO;
import cohappy.backend.model.dto.request.MoveMoneyDTO;
import cohappy.backend.model.dto.request.MoveMoneyOperationEnum;
import cohappy.backend.model.dto.request.SendMoneyDTO;
import cohappy.backend.model.dto.response.PortfolioDTO;
import cohappy.backend.service.PortfolioService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static cohappy.backend.model.OperationResultMessages.OPERATION_COMPLETED;

@RestController
@RequestMapping("/api/portafolio")
@Slf4j
@AllArgsConstructor
public class PortafolioController {
    private final PortfolioService portafolioService;

    @GetMapping("/{userCode}")
    public ResponseEntity<PortfolioDTO> getUserPortafolio(@PathVariable String userCode) {
        try {
            PortfolioDTO portfolio = portafolioService.getUserPortfolio(userCode);
            return ResponseEntity.ok(portfolio);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/money/move")
    public ResponseEntity<String> patchMoveMoney(@RequestBody MoveMoneyDTO moveMoneyDTO) {
        try {
            if (moveMoneyDTO.getOperation() == MoveMoneyOperationEnum.SEND) {
                portafolioService.addMoneyToPortfolio(moveMoneyDTO);
                return ResponseEntity.ok(OPERATION_COMPLETED);
            } else {
                portafolioService.retrieveMoneyFromPortfolio(moveMoneyDTO);
                return ResponseEntity.ok(OPERATION_COMPLETED);
            }
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FundExcededException | FundInsufficientException e) {
            return ResponseEntity.unprocessableContent().body(e.getMessage());
        } catch (IllegalInputException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PatchMapping("/money/send")
    public ResponseEntity<String> patchSendMoney(@RequestBody SendMoneyDTO sendMoneyDTO) {
        try {
            portafolioService.sendMoney(sendMoneyDTO);
            return ResponseEntity.ok(OPERATION_COMPLETED);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FundExcededException | FundInsufficientException e) {
            return ResponseEntity.unprocessableContent().body(e.getMessage());
        } catch (IllegalInputException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/debt/create")
    public ResponseEntity<String> createDebtMoney(@RequestBody CreateDebtDTO createDebtDTO) {
        try {
            portafolioService.createDebtMoney(createDebtDTO);
            return ResponseEntity.ok(OPERATION_COMPLETED);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalInputException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/debt/delete/{debtId}")
    public ResponseEntity<String> deleteDebt(@PathVariable String debtId) {
        try {
            portafolioService.deleteDebt(debtId);
            return ResponseEntity.ok(OPERATION_COMPLETED);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
