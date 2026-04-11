package cohappy.backend.model;

import lombok.Data;

import java.util.List;

@Data
public class Portafolio {
    private int amount;
    private Currency currency;
    private List<Debt> debts;
}
