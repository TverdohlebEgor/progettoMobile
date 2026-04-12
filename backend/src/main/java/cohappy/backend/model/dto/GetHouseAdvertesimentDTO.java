package cohappy.backend.model.dto;

import cohappy.backend.model.HouseState;
import lombok.Data;

import java.util.List;

@Data
public class GetHouseAdvertesimentDTO {
    private String houseCode;
    private List<byte[]> images;
    private int costPerMonth;
    private String country;
    private String region;
    private String street;
    private int civicNumber;
    private HouseState state;
    private String publishedByCode;
    private List<byte[]> publishedByImages;
    private String publishedByEmail;
    private String publishedByPhoneNumber;
}
