package cohappy.backend.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateHouseDTO {
    private String userCode;
    private List<byte[]> images;
    private Integer costPerMonth;
    private String country;
    private String region;
    private String street;
    private Integer civicNumber;
}
