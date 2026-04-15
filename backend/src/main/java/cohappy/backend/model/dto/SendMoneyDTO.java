package cohappy.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMoneyDTO {
    private String senderUserCode;
    private String receiverUserCode;
    private float amount;
}

