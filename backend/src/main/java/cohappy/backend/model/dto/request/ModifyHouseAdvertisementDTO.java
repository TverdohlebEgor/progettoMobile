package cohappy.backend.model.dto.request;

import cohappy.backend.model.HouseState;
import lombok.Data;

@Data
public class ModifyHouseAdvertisementDTO {
    private String houseCode;
    private HouseState state;
    private String description;
}
