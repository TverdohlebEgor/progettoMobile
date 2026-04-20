package cohappy.backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetChoreDTO {
    private String choreCode;
    private String assignedTo;
    private String assignedToName;
    private byte[] assignedToImage;
    private boolean completed;
    private String createdBy;
    private String houseCode;
    private String name;
    private String description;
}
