package cohappy.backend.service;

import cohappy.backend.exceptions.*;
import cohappy.backend.mappers.DebtMapper;
import cohappy.backend.mappers.PortafolioMapper;
import cohappy.backend.model.Debt;
import cohappy.backend.model.NotificationType;
import cohappy.backend.model.UserAccount;
import cohappy.backend.model.dto.request.*;
import cohappy.backend.model.dto.response.DebtDTO;
import cohappy.backend.model.dto.response.PortfolioDTO;
import cohappy.backend.repositories.DebtRepository;
import cohappy.backend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static cohappy.backend.model.OperationResultMessages.*;

@AllArgsConstructor
@Service
public class PortfolioService {
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final DebtRepository debtRepository;
    private final PortafolioMapper mapper = new PortafolioMapper();
    private final DebtMapper debtMapper = new DebtMapper();
    private static final float MAX_MONEY_ACCOUNT = 1000000f;

    public PortfolioDTO getUserPortfolio(String userCode) {
        UserAccount userAccount = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(userCode)));

        List<DebtDTO> debts = debtRepository.findByCreditorUserCode(userCode).stream()
                .map(debtMapper::debtToDTO)
                .toList();

        return mapper.portfolioToDTO(userAccount.getPortfolio(), debts);
    }

    public void addMoneyToPortfolio(MoveMoneyDTO moveMoneyDTO) {
        if (moveMoneyDTO.getAmount() == 0) {
            throw new IllegalInputException(MOVE_AMOUNT_ZERO);
        }

        String userCode = moveMoneyDTO.getUserCode();

        UserAccount userAccount = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(userCode)));

        float currenctAmount = userAccount.getPortfolio().getAmount();

        if (currenctAmount + moveMoneyDTO.getAmount() > MAX_MONEY_ACCOUNT) {
            throw new FundExcededException("The limit for an account is %s".formatted(MAX_MONEY_ACCOUNT));
        }

        userAccount.getPortfolio().setAmount(currenctAmount + moveMoneyDTO.getAmount());
        userRepository.save(userAccount);
    }

    public void retrieveMoneyFromPortfolio(MoveMoneyDTO moveMoneyDTO) {
        if (moveMoneyDTO.getAmount() == 0) {
            throw new IllegalInputException(MOVE_AMOUNT_ZERO);
        }

        String userCode = moveMoneyDTO.getUserCode();

        UserAccount userAccount = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(userCode)));

        float currenctAmount = userAccount.getPortfolio().getAmount();

        if (currenctAmount - moveMoneyDTO.getAmount() < 0) {
            throw new FundInsufficientException("The account portfolio has only %s euro".formatted(currenctAmount));
        }

        userAccount.getPortfolio().setAmount(currenctAmount - moveMoneyDTO.getAmount());
        userRepository.save(userAccount);
    }

    public void sendMoney(SendMoneyDTO sendMoneyDTO) {
        if (sendMoneyDTO.getAmount() == 0) {
            throw new IllegalInputException(MOVE_AMOUNT_ZERO);
        }

        if (sendMoneyDTO.getSenderUserCode().equals(sendMoneyDTO.getReceiverUserCode())) {
            throw new IllegalInputException("Sender and receiver can't be the same person");
        }

        MoveMoneyDTO addMoneyRequest = new MoveMoneyDTO(
                sendMoneyDTO.getReceiverUserCode(),
                MoveMoneyOperationEnum.SEND,
                sendMoneyDTO.getAmount()
        );

        MoveMoneyDTO retrieveMoneyRequest = new MoveMoneyDTO(
                sendMoneyDTO.getSenderUserCode(),
                MoveMoneyOperationEnum.RETRIEVE,
                sendMoneyDTO.getAmount()
        );

        addMoneyToPortfolio(addMoneyRequest);
        retrieveMoneyFromPortfolio(retrieveMoneyRequest);
    }

    public void createDebtMoney(CreateDebtDTO createDebtDTO) {
        String creditorUserCode = createDebtDTO.getCreditorCode();
        Map<String, Boolean> debtorsUserCode = createDebtDTO.getReceiverCode();

        if (debtorsUserCode.containsKey(creditorUserCode)) {
            throw new IllegalInputException("Sender and receiver can't be the same person");
        }

        userRepository.findByUserCode(creditorUserCode)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(creditorUserCode)));

        for (String debtorUserCode : debtorsUserCode.keySet()) {
            userRepository.findByUserCode(debtorUserCode)
                    .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(debtorUserCode)));
        }

        String debtId = UUID.randomUUID().toString();

        Debt creditorDebt = new Debt(
                debtId,
                creditorUserCode,
                debtorsUserCode,
                createDebtDTO.getIsCreatorIncluded(),
                createDebtDTO.getAmount(),
                createDebtDTO.getDescription(),
                createDebtDTO.getDebtType()
        );
        debtRepository.save(creditorDebt);

        notificationService.createNotification(
                NotificationType.PORTFOLIO,
                "Nuovo credito",
                createDebtDTO.getDescription(),
                null,
                creditorUserCode
        );

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
        debtRepository.findByDebtId(debtId)
                .orElseThrow(() -> new NotFoundException(DEBT_NOT_FOUND.formatted(debtId)));
        debtRepository.deleteByDebtId(debtId);
    }

    public void patchDebtPaid(PatchDebtPaidDTO patchDebtPaidDTO) {
        boolean patched = false;
        Debt debt = debtRepository.findByDebtId(patchDebtPaidDTO.getDebtId()).orElseThrow(
                () -> new NotFoundException(DEBT_NOT_FOUND.formatted(patchDebtPaidDTO.getDebtId()))
        );

        if (debt.getDebtorsCode().containsKey(patchDebtPaidDTO.getReceiverCode())) {
            debt.getDebtorsCode().put(
                    patchDebtPaidDTO.getReceiverCode(),
                    patchDebtPaidDTO.getNewState()
            );
            patched = true;
        }

        if (!patched) {
            throw new NoPatchException(NO_PATCH.formatted("the new state given"));
        }
        debtRepository.save(debt);
    }

    public List<Debt> findByCreditorUserCode(String userCode){
        return debtRepository.findByCreditorUserCode(userCode);
    }

    public List<Debt> findByUserCodeInDebtors(String userCode){
        return debtRepository.findByUserCodeInDebtors(userCode);
    }

}
