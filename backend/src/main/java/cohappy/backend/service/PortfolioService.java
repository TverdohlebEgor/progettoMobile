package cohappy.backend.service;

import cohappy.backend.exceptions.FundExcededException;
import cohappy.backend.exceptions.FundInsufficientException;
import cohappy.backend.exceptions.IllegalInputException;
import cohappy.backend.exceptions.NotFoundException;
import cohappy.backend.model.Debt;
import cohappy.backend.model.Portfolio;
import cohappy.backend.model.UserAccount;
import cohappy.backend.model.dto.CreateDebtDTO;
import cohappy.backend.model.dto.MoveMoneyDTO;
import cohappy.backend.model.dto.MoveMoneyOperationEnum;
import cohappy.backend.model.dto.SendMoneyDTO;
import cohappy.backend.repositories.DebtRepository;
import cohappy.backend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static cohappy.backend.model.OperationResultMessages.*;

@AllArgsConstructor
@Service
public class PortfolioService {
    private final UserRepository userRepository;
    private final DebtRepository debtRepository;
    private static final float MAX_MONEY_ACCOUNT = 1000000f;

    public Portfolio getUserPortfolio(String userCode) {
        UserAccount userAccount = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(userCode)));

        return userAccount.getPortfolio();
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
        String senderUserCode = createDebtDTO.getSenderUserCode();
        String receiverUserCode = createDebtDTO.getReceiverUserCode();

        if (receiverUserCode.equals(senderUserCode)) {
            throw new IllegalInputException("Sender and receiver can't be the same person");
        }

        UserAccount senderUserAccount = userRepository.findByUserCode(senderUserCode)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(senderUserCode)));

        UserAccount receiverUserAccount = userRepository.findByUserCode(receiverUserCode)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(receiverUserCode)));

        String senderDebtId = UUID.randomUUID().toString();
        String receiverDebtId = UUID.randomUUID().toString();

        Debt senderDebt = new Debt(
                senderDebtId,
                receiverDebtId,
                senderUserCode,
                receiverUserCode,
                createDebtDTO.getAmount(),
                createDebtDTO.getDescription()
        );

        Debt receiverDebt = new Debt(
                receiverDebtId,
                senderDebtId,
                receiverUserCode,
                senderUserCode,
                createDebtDTO.getAmount(),
                createDebtDTO.getDescription()
        );

        debtRepository.save(senderDebt);
        debtRepository.save(receiverDebt);

        senderUserAccount.getPortfolio().getDebts().add(senderDebt);
        receiverUserAccount.getPortfolio().getDebts().add(receiverDebt);

        userRepository.save(senderUserAccount);
        userRepository.save(receiverUserAccount);
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
