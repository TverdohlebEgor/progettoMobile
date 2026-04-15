package cohappy.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class UserAccount extends Anagrafica{
    @Id
    private String userCode;
    private List<byte[]> images;
    private String email;
    private String phoneNumber;
    private String password;
    private Portfolio portfolio;
}
