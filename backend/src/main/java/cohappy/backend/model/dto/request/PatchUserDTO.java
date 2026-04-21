package cohappy.backend.model.dto.request;

import cohappy.backend.model.Anagrafica;
import cohappy.backend.model.Portfolio;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.List;

@Data
public class PatchUserDTO{
    private String userCode;

    private String name;
    private String surname;
    private LocalDate birthDate;
    private Integer age;
    private List<byte[]> images;
    private String email;
    private String phoneNumber;
    private String password;
}
