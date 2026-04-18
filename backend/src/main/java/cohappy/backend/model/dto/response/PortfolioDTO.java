package cohappy.backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PortfolioDTO {
    private float amount;
    private List<DebtDTO> debts;
}
