package cohappy.backend.mappers;

import cohappy.backend.model.House;
import cohappy.backend.model.Location;
import cohappy.backend.model.dto.CreateHouseDTO;
import cohappy.backend.model.dto.GetHouseDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HouseMapper {
    public GetHouseDTO houseToGetDTO(House house){
        GetHouseDTO result = new GetHouseDTO();

        result.setHouseCode(house.getHouseCode());
        result.setAdmins(house.getAdmins());
        result.setUsers(house.getUsers());
        result.setImages(house.getImages());
        result.setCostPerMonth(house.getCostPerMonth());
        result.setCountry(house.getLocation().getCountry());
        result.setRegion(house.getLocation().getRegion());
        result.setStreet(house.getLocation().getStreet());
        result.setCivicNumber(house.getLocation().getCivicNumber());

        return result;
    }

    public House CreateDTOToHouse(CreateHouseDTO createHouseDTO){
        House result = new House();
        result.setHouseCode(UUID.randomUUID().toString());
        result.setAdmins(List.of(createHouseDTO.getUserCode()));
        result.setUsers(new ArrayList<>());
        result.setImages(createHouseDTO.getImages());
        if(result.getImages() == null){
            result.setImages(new ArrayList<>());
        }
        result.setCostPerMonth(createHouseDTO.getCostPerMonth());
        result.setChores(new ArrayList<>());

        Location location = new Location();
        location.setStreet(createHouseDTO.getStreet());
        location.setRegion(createHouseDTO.getRegion());
        location.setCountry(createHouseDTO.getCountry());
        location.setCivicNumber(createHouseDTO.getCivicNumber());

        result.setLocation(location);
        return result;
    }
}
