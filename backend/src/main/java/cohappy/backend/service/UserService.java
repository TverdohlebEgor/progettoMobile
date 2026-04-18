package cohappy.backend.service;

import cohappy.backend.exceptions.IllegalInputException;
import cohappy.backend.exceptions.NotFoundException;
import cohappy.backend.mappers.UserMapper;
import cohappy.backend.model.UserAccount;
import cohappy.backend.model.dto.request.LoginDTO;
import cohappy.backend.model.dto.request.RegisterDTO;
import cohappy.backend.model.dto.response.UserAccountDTO;
import cohappy.backend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static cohappy.backend.util.StringCheckUtil.isEmptyString;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper = new UserMapper();

    public UserAccountDTO getUserProfile(String userCode) {
        Optional<UserAccount> account = userRepository.findByUserCode(userCode);
        return userMapper.userAcountToDTO(account.orElseThrow(
                () -> new NotFoundException("user with code %s not found".formatted(userCode))
        ));
    }

    public boolean deleteProfile(String userCode) {
        return userRepository.deleteByUserCode(userCode) > 0;
    }

    public String login(LoginDTO loginDTO) {
        if (isEmptyString(loginDTO.getPassword())) {
            throw new IllegalInputException("Password can't be empty nor blank");
        }

        if (isEmptyString(loginDTO.getEmail()) && isEmptyString(loginDTO.getPhoneNumber())) {
            throw new IllegalInputException("At least one between email and phone number must be provided");
        }

        Optional<UserAccount> account = Optional.empty();
        if (loginDTO.getEmail() != null && !loginDTO.getEmail().isBlank()) {
            account = userRepository.findByEmail(loginDTO.getEmail());
        }

        if (account.isEmpty() && loginDTO.getPhoneNumber() != null && !loginDTO.getPhoneNumber().isBlank()) {
            account = userRepository.findByPhoneNumber(loginDTO.getPhoneNumber());
        }

        if (account.isEmpty()) {
            throw new NotFoundException("no account founded with email:%s phone number:%s".formatted(loginDTO.getEmail(), loginDTO.getPhoneNumber()));
        }

        Optional<String> userCode = Optional.of(account.get().getUserCode());

        Optional<UserAccount> finalAccount = account;
        return userCode.orElseThrow(
                () -> new IllegalStateException("Founded a user with user code null %s".formatted(finalAccount.toString()))
        );

    }

    public String register(RegisterDTO registerDTO) {
        validateInput(registerDTO.getEmail(), "email");
        validateInput(registerDTO.getPhoneNumber(), "phone number");
        validateInput(registerDTO.getPassword(), "password");

        Optional<UserAccount> account = userRepository.findByEmail(registerDTO.getEmail());
        if (account.isPresent()) {
            throw new IllegalInputException("The email %s is already connected to an account".formatted(registerDTO.getEmail()));
        }

        account = userRepository.findByPhoneNumber(registerDTO.getPhoneNumber());
        if (account.isPresent()) {
            throw new IllegalInputException("The phone number %s is already connected to an account".formatted(registerDTO.getPhoneNumber()));
        }

        UserAccount newAccount = userMapper.registerDTOtoUserAccount(registerDTO);
        userRepository.save(newAccount);
        return newAccount.getUserCode();
    }

    private void validateInput(String str, String fieldName) {
        if (isEmptyString(str)) {
            throw new IllegalInputException("the value %s must be filled and not empty".formatted(fieldName));
        }
    }
}
