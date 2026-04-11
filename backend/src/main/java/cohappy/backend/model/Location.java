package cohappy.backend.model;

import lombok.Data;

@Data
public class Location {
    private String country;
    private String region;
    private String street;
    private int civicNumber;
}
