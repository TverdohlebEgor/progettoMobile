package cohappy.backend.model;

import lombok.Data;

import java.util.List;

@Data
public class House {
   //house code is unique
   private String houseCode;
   private HouseState state;
   private UserAccount publishedBy;
   private List<UserAccount> admins;
   private List<UserAccount> users;
   private List<byte[]> images;
   private int costPerMonth;
   private List<HouseChore> chores;
   private Location location;
}
