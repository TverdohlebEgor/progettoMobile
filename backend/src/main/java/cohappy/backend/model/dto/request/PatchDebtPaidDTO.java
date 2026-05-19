package cohappy.backend.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatchDebtPaidDTO {
    private String debtId;
    private String receiverCode;
    private Boolean newState;
}

