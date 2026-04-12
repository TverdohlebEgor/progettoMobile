package cohappy.backend.model.dto;

import cohappy.backend.model.HouseState;
import lombok.Data;

@Data
public class CreateHouseAdvertisementDTO {
    private String houseCode;
    private HouseState state;
    private String publishedBy;
}
