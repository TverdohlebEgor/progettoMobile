package cohappy.backend.model.dto.request;

import lombok.Data;

@Data
public class RemoveAdminDTO {
    private String houseCode;
    private String userCode;
}
