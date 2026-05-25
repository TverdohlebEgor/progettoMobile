package cohappy.backend.service;

import cohappy.backend.exceptions.IllegalInputException;
import cohappy.backend.exceptions.NoPatchException;
import cohappy.backend.exceptions.NotFoundException;
import cohappy.backend.model.Debt;
import cohappy.backend.model.NotificationType;
import cohappy.backend.model.dto.request.CreateDebtDTO;
import cohappy.backend.model.dto.request.PatchDebtPaidDTO;
import cohappy.backend.repositories.DebtRepository;
import cohappy.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

import static cohappy.backend.model.OperationResultMessages.*;

@Service
public class DebtService {
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final DebtRepository debtRepository;

    public DebtService(NotificationService notificationService, UserRepository userRepository, DebtRepository debtRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.debtRepository = debtRepository;
    }

    public void createDebtMoney(CreateDebtDTO createDebtDTO) {
        String creditorUserCode = createDebtDTO.getCreditorCode();
        Map<String, Boolean> debtorsUserCode = createDebtDTO.getReceiverCode();

        if (debtorsUserCode.containsKey(creditorUserCode)) {
            throw new IllegalInputException("Sender and receiver can't be the same person");
        }

        userRepository.findByUserCode(creditorUserCode)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(creditorUserCode)));

        for (String receiverUserCode : debtorsUserCode.keySet()) {
            userRepository.findByUserCode(receiverUserCode)
                    .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(receiverUserCode)));
        }

        String debtId = UUID.randomUUID().toString();

        Debt debt = new Debt(
                debtId,
                creditorUserCode,
                debtorsUserCode,
                createDebtDTO.getIsCreatorIncluded(),
                createDebtDTO.getAmount(),
                createDebtDTO.getDescription(),
                createDebtDTO.getDebtType()
        );
        debtRepository.save(debt);


        for (String debtorUserCode : debtorsUserCode.keySet()) {
            notificationService.createNotification(
                    NotificationType.PORTFOLIO,
                    "Nuovo debito",
                    createDebtDTO.getDescription(),
                    null,
                    debtorUserCode
            );
        }
    }

    public void deleteDebt(String debtId) {
        debtRepository.findByDebtId(debtId).orElseThrow(
                () -> new NotFoundException(DEBT_NOT_FOUND.formatted(debtId))
        );
        debtRepository.deleteByDebtId(debtId);
    }

    public void patchDebtPaid(PatchDebtPaidDTO patchDebtPaidDTO) {
        Debt debt = debtRepository.findByDebtId(patchDebtPaidDTO.getDebtId()).orElseThrow(
                () -> new NotFoundException(DEBT_NOT_FOUND.formatted(patchDebtPaidDTO.getDebtId()))
        );

        if(debt.getDebtorsCode().containsKey(patchDebtPaidDTO.getReceiverCode())){
            debt.getDebtorsCode().put(
                    patchDebtPaidDTO.getReceiverCode(),
                    patchDebtPaidDTO.getNewState()
            );
        } else {
            throw new NoPatchException(NO_PATCH.formatted("the new state given"));
        }
        debtRepository.save(debt);
    }
}
