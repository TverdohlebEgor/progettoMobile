package cohappy.backend.model.dto.request;

import cohappy.backend.model.DebtType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDebtDTO {
    private String creditorCode;
    private Map<String,Boolean> receiverCode;
    private float amount;
    private String description;
    private DebtType debtType;
}

