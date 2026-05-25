package cohappy.backend.mappers;

import cohappy.backend.model.Debt;
import cohappy.backend.model.dto.response.DebtDTO;

public class DebtMapper {
    public DebtDTO debtToDTO(Debt debt){
        return new DebtDTO(
                debt.getDebtId(),
                debt.getCreditorUserCode(),
                debt.getDebtorsCode(),
                debt.isCreatorIncluded(),
                debt.getAmount(),
                debt.getDescription(),
                debt.getDebtType()
        );
    }
}
