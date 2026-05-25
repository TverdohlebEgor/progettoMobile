package cohappy.backend.model.dto.response;

import cohappy.backend.model.DebtType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebtDTO {
    @Id
    private String debtId;
    private String creditorUserCode;
    private Map<String,Boolean> debtorsUserCode;
    private boolean isCreatorIncluded;
    private float amount;
    private String description;
    private DebtType debtType;
}
