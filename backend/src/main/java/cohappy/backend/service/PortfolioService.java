package cohappy.backend.service;

import cohappy.backend.exceptions.FundExcededException;
import cohappy.backend.exceptions.FundInsufficientException;
import cohappy.backend.exceptions.IllegalInputException;
import cohappy.backend.exceptions.NotFoundException;
import cohappy.backend.mappers.PortafolioMapper;
import cohappy.backend.model.Debt;
import cohappy.backend.model.NotificationType;
import cohappy.backend.model.UserAccount;
import cohappy.backend.model.dto.request.CreateDebtDTO;
import cohappy.backend.model.dto.request.MoveMoneyDTO;
import cohappy.backend.model.dto.request.MoveMoneyOperationEnum;
import cohappy.backend.model.dto.request.SendMoneyDTO;
import cohappy.backend.model.dto.response.PortfolioDTO;
import cohappy.backend.repositories.DebtRepository;
import cohappy.backend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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
    private static final float MAX_MONEY_ACCOUNT = 1000000f;

    public PortfolioDTO getUserPortfolio(String userCode) {
        UserAccount userAccount = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(userCode)));

        return mapper.portfolioToDTO(userAccount.getPortfolio());
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

        UserAccount senderUserAccount = userRepository.findByUserCode(creditorUserCode)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(creditorUserCode)));

        for (String receiverUserCode : debtorsUserCode.keySet()) {

        }

        String creditorDebtId = UUID.randomUUID().toString();
        String debtorDebtId = UUID.randomUUID().toString();

        Debt creditorDebt = new Debt(
                creditorDebtId,
                debtorDebtId,
                creditorUserCode,
                debtorsUserCode,
                createDebtDTO.getAmount(),
                createDebtDTO.getDescription(),
                createDebtDTO.getDebtType()
        );
        debtRepository.save(creditorDebt);
        senderUserAccount.getPortfolio().getDebts().add(creditorDebt);
        userRepository.save(senderUserAccount);

        for(String debtorUserCode: debtorsUserCode.keySet()){
            UserAccount debtorAcount = userRepository.findByUserCode(debtorUserCode)
                    .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(debtorUserCode)));
            Debt debtorDebt = new Debt(
                    debtorDebtId,
                    creditorDebtId,
                    debtorUserCode,
                    Map.of(creditorUserCode,true),
                    createDebtDTO.getAmount(),
                    createDebtDTO.getDescription(),
                    createDebtDTO.getDebtType()
            );
            debtRepository.save(debtorDebt);
            debtorAcount.getPortfolio().getDebts().add(debtorDebt);
            userRepository.save(debtorAcount);

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
        Debt debt = debtRepository.findByDebtId(debtId).orElseThrow(
                () -> new NotFoundException(DEBT_NOT_FOUND.formatted(debtId))
        );

        String linkedDebtId = debt.getLinkedDebtId();

        debtRepository.findByDebtId(linkedDebtId).orElseThrow(
                () -> new NotFoundException(DEBT_NOT_FOUND.formatted(linkedDebtId))
        );

        debtRepository.deleteByDebtId(debtId);
        debtRepository.deleteByDebtId(linkedDebtId);

        List<String> idsToRemove = Arrays.asList(debtId, linkedDebtId);

        userRepository.findByPortfolioDebtsDebtIdIn(idsToRemove)
                .forEach(user -> {
                    user.getPortfolio().getDebts().removeIf(d ->
                            idsToRemove.contains(d.getDebtId())
                    );
                    userRepository.save(user);
                });
    }
}
