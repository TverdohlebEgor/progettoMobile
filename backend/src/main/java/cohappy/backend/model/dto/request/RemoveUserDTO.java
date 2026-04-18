package cohappy.backend.model.dto.request;

import lombok.Data;

@Data
public class RemoveUserDTO {
    private String houseCode;
    private String userCode;
}
