package cohappy.backend.mappers;

import cohappy.backend.model.Debt;
import cohappy.backend.model.Portfolio;
import cohappy.backend.model.dto.response.DebtDTO;
import cohappy.backend.model.dto.response.PortfolioDTO;

public class PortafolioMapper {
    public PortfolioDTO portfolioToDTO(Portfolio portfolio){
        return new PortfolioDTO(
               portfolio.getAmount(),
                portfolio.getDebts().stream().map(this::debtToDTO).toList()
        );
    }

    public DebtDTO debtToDTO(Debt debt){
        return new DebtDTO(
                debt.getDebtId(),
                debt.getLinkedDebtId(),
                debt.getDebtorUserCode(),
                debt.getBeneficiaryUserCode(),
                debt.getAmount(),
                debt.getDescription()
        );
    }
}
