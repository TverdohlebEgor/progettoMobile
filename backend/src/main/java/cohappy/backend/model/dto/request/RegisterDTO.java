package cohappy.backend.model.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class RegisterDTO {
    private List<byte[]> images;
    private String email;
    private String phoneNumber;
    private String password;
}
