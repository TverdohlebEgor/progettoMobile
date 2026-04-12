package cohappy.backend.model;

import lombok.Data;

@Data
public class Debt {
    private String debtorUserCode;
    private String beneficiaryUserCode;
    private int amount;
}
