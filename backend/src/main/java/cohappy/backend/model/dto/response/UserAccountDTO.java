package cohappy.backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class UserAccountDTO{
    private String name;
    private String surname;
    private LocalDate birthDate;
    private int age;
    @Id
    private String userCode;
    private List<byte[]> images;
    private String email;
    private String phoneNumber;
    private String password;
    private PortfolioDTO portfolio;
}
