package cohappy.backend.model.dto.request;

import cohappy.backend.model.HouseState;
import lombok.Data;

import java.util.List;

@Data
public class ModifyHouseAdvertisementDTO {
    private List<byte[]> immages;
    private String houseCode;
    private HouseStateDTO state;
    private String description;
}
