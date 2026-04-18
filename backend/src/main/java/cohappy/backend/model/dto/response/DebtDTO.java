package cohappy.backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebtDTO {
    @Id
    private String debtId;
    private String linkedDebtId;
    private String debtorUserCode;
    private String beneficiaryUserCode;
    private float amount;
    private String description;
}
