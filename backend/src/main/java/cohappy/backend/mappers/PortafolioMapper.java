package cohappy.backend.mappers;

import cohappy.backend.model.Portfolio;
import cohappy.backend.model.dto.response.DebtDTO;
import cohappy.backend.model.dto.response.PortfolioDTO;

import java.util.List;

public class PortafolioMapper {
    public PortfolioDTO portfolioToDTO(Portfolio portfolio, List<DebtDTO> debts){
        return new PortfolioDTO(
               portfolio.getAmount(),
               debts
        );
    }
}
