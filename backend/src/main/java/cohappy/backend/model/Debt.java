package cohappy.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Debt {
    @Id
    private String debtId;
    private String creditorUserCode;
    private Map<String,Boolean> debtorsCode;
    private boolean isCreatorIncluded;
    private float amount;
    private String description;
    private DebtType debtType;
}
