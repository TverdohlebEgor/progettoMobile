package cohappy.backend.model.dto.request;

import cohappy.backend.model.HouseState;
import lombok.Data;

@Data
public class CreateHouseAdvertisementDTO {
    private String houseCode;
    private HouseStateDTO state;
    private String publishedBy;
    private String description;
}
