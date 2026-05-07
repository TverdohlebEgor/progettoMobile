package cohappy.backend.model.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateHouseAdvertisementDTO {
    private List<byte[]> images;
    private String houseCode;
    private HouseStateDTO state;
    private String publishedBy;
    private String description;
}
