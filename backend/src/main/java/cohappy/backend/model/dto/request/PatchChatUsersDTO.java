package cohappy.backend.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatchChatUsersDTO {
    private String chatCode;
    private List<String> usersCode;
    private PatchChatUsersOperationEnum operation;
}
