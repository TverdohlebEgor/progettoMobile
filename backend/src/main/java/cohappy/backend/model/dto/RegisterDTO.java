package cohappy.backend.model.dto;

import cohappy.backend.model.Currency;
import lombok.Data;

import java.util.List;

@Data
public class RegisterDTO {
    private List<byte[]> images;
    private String email;
    private String phoneNumber;
    private String password;
    private Currency currency;
}
