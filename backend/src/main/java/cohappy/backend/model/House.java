package cohappy.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class House {
   @Id
   private String houseCode;
   private List<String> admins;
   private List<String> users;
   private List<byte[]> images;
   private int costPerMonth;
   private List<HouseChore> chores;
   private Location location;

   public House copy(){
      House result = new House();
      result.setHouseCode(houseCode);
      result.setAdmins(admins);
      result.setUsers(users);
      result.setImages(images);
      result.setCostPerMonth(costPerMonth);
      result.setChores(chores);
      result.setLocation(location);
      return result;
   }

   public boolean isEqualTo(House house){
      for(byte[] image : images){
         if(!house.getImages().contains(image)){
            return false;
         }
      }
      return houseCode.equals(house.getHouseCode()) &&
              costPerMonth == house.getCostPerMonth() &&
              location.getCountry().equals(house.getLocation().getCountry()) &&
              location.getRegion().equals(house.getLocation().getRegion()) &&
              location.getStreet().equals(house.getLocation().getStreet()) &&
              location.getCivicNumber() == house.getLocation().getCivicNumber();
   }
}
