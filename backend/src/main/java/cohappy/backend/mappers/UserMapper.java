package cohappy.backend.mappers;

import cohappy.backend.model.Portfolio;
import cohappy.backend.model.UserAccount;
import cohappy.backend.model.dto.RegisterDTO;

import java.util.ArrayList;
import java.util.UUID;

public class UserMapper {
    public UserAccount registerDTOtoUserAccount(RegisterDTO registerDTO) {
        UserAccount result = new UserAccount();

        result.setUserCode(UUID.randomUUID().toString());
        result.setImages(registerDTO.getImages());
        result.setEmail(registerDTO.getEmail());
        result.setPhoneNumber(registerDTO.getPhoneNumber());
        result.setPassword(registerDTO.getPassword());

        Portfolio resultPortfolio = new Portfolio();
        resultPortfolio.setAmount(0);
        resultPortfolio.setDebts(new ArrayList<>());

        result.setPortfolio(resultPortfolio);

        return result;
    }
}
