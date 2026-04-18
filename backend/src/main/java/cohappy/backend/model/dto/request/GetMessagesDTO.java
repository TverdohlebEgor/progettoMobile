package cohappy.backend.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetMessagesDTO {
    private Integer startProgressive;
    private Integer endProgressive;
}
