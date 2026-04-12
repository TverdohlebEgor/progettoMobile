package cohappy.backend.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ModifyHouseDTO {
    private String houseCode;
    private List<byte[]> images;
    private Integer costPerMonth;
    private String country;
    private String region;
    private String street;
    private Integer civicNumber;

    public boolean areAllNull(){
        return
                (images == null || images.isEmpty()) &&
                costPerMonth == null &&
                country == null &&
                region == null &&
                street == null &&
                civicNumber == null;
    }
}
