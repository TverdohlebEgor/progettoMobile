package cohappy.backend.model.dto.request;

import lombok.Data;

@Data
public class LoginDTO {
   private String email;
   private String phoneNumber;
   private String password;
}
