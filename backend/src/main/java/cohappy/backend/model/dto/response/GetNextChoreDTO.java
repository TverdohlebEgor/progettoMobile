package cohappy.backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetNextChoreDTO {
    private String choreCode;
    private String name;
    private String assignedTo;
    private LocalDate date;
    private boolean completed;
}
