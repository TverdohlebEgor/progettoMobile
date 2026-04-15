package cohappy.backend.model;

import lombok.Data;

import java.util.List;

@Data
public class Portfolio {
    private float amount;
    private List<Debt> debts;
}
