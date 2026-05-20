package cohappy.backend.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateChoreDTO {
    private List<LocalDate> days;
    private Map<LocalDate, String> assignedTo;
    private String createdBy;
    private String houseCode;
    private String name;
    private String description;
}
