package cohappy.backend.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMessageDTO {
    private String chatCode;
    private String message;
    private byte[] messageImmage;
    private String userCode;
}
