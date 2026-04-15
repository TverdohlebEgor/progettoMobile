package cohappy.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDebtDTO {
    private String senderUserCode;
    private String receiverUserCode;
    private float amount;
    private String description;
}

