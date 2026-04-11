package cohappy.backend.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class HouseChore {
    private List<LocalDate> days;
    private Map<LocalDate, UserAccount> assignedTo;
    private Map<LocalDate, Boolean> completed;
    private UserAccount createdBy;
    private String name;
    private String description;
}
