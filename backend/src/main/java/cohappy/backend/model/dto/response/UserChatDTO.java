package cohappy.backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserChatDTO {
    private String chatCode;
    private List<String> participating;
    private String name;
    private byte[] immage;
}
