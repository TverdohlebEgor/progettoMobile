package cohappy.backend.model.dto.request;

import cohappy.backend.model.HouseState;
import lombok.Data;

@Data
public class ModifyHouseAdvertisementDTO {
    private String houseCode;
    private HouseStateDTO state;
    private String description;
}
