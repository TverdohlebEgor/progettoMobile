package cohappy.backend.model;

import lombok.Data;

@Data
public class Debt {
    private UserAccount debtor;
    private UserAccount beneficiary;
    private int amount;
}
