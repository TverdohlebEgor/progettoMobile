package cohappy.backend.mappers;

import cohappy.backend.model.Portfolio;
import cohappy.backend.model.UserAccount;
import cohappy.backend.model.dto.request.RegisterDTO;
import cohappy.backend.model.dto.response.UserAccountDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class UserMapper {

    private final PortafolioMapper portafolioMapper = new PortafolioMapper();

    public UserAccount registerDTOtoUserAccount(RegisterDTO registerDTO) {
        UserAccount result = new UserAccount();

        result.setName(registerDTO.getName());
        result.setSurname(registerDTO.getSurname());
        result.setBirthDate(LocalDate.parse(registerDTO.getBirthDate()));
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

    public UserAccountDTO userAcountToDTO(UserAccount userAccount){
        return new UserAccountDTO(
                userAccount.getName(),
                userAccount.getSurname(),
                userAccount.getBirthDate(),
                userAccount.getCf(),
                userAccount.getAge(),
                userAccount.getUserCode(),
                userAccount.getImages(),
                userAccount.getEmail(),
                userAccount.getPhoneNumber(),
                userAccount.getPassword(),
                portafolioMapper.portfolioToDTO(userAccount.getPortfolio())
        );
    }
}
