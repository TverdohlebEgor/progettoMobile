package cohappy.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class UserAccount extends Anagrafica{
    //user code is unique
    private String userCode;
    private List<byte[]> images;
    private String email;
    private String phoneNumber;
    private String password;
    private Portafolio portafolio;
}
