package cohappy.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MoveMoneyDTO {
    private String userCode;
    private MoveMoneyOperationEnum operation;
    private float amount;
}

