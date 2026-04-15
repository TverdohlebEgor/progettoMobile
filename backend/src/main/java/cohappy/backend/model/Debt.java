package cohappy.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Debt {
    @Id
    private String debtId;
    private String linkedDebtId;
    private String debtorUserCode;
    private String beneficiaryUserCode;
    private float amount;
    private String description;
}
