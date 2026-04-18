package cohappy.backend.model.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class GetHouseDTO {
    private String houseCode;
    private List<String> admins;
    private List<String> users;
    private List<byte[]> images;
    private int costPerMonth;
    private String country;
    private String region;
    private String street;
    private int civicNumber;
}
