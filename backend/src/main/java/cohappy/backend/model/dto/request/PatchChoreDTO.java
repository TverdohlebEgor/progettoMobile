package cohappy.backend.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatchChoreDTO {
    private String choreCode;
    private LocalDate day;
    private String assignedTo;
    private Boolean completed;
    private String houseCode;
    private String name;
    private String description;
}
