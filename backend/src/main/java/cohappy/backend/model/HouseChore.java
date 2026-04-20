package cohappy.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class HouseChore {
    @Id
    private String choreCode;
    private List<LocalDate> days;
    private Map<LocalDate, String> assignedTo;
    private Map<LocalDate, Boolean> completed;
    private String createdBy;
    private String houseCode;
    private String name;
    private String description;
}
