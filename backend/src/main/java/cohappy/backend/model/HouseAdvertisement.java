package cohappy.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class HouseAdvertisement {
   @Id
   private String houseCode;
   private HouseState state;
   private String publishedBy;
   private String description;
}
