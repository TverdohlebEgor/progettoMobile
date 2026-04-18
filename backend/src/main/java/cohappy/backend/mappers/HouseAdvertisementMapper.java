package cohappy.backend.mappers;

import cohappy.backend.model.House;
import cohappy.backend.model.HouseAdvertisement;
import cohappy.backend.model.UserAccount;
import cohappy.backend.model.dto.response.GetHouseAdvertesimentDTO;

import java.util.ArrayList;

public class HouseAdvertisementMapper {

    public GetHouseAdvertesimentDTO houseAdvertesimentToGetDTO(
            HouseAdvertisement houseAdvertisement,
            House house,
            UserAccount userAccount
    ) {
        GetHouseAdvertesimentDTO result = new GetHouseAdvertesimentDTO();

        result.setHouseCode(houseAdvertisement.getHouseCode());
        result.setImages(house.getImages());
        if(result.getImages() == null){
            result.setImages(new ArrayList<>());
        }
        result.setCostPerMonth(house.getCostPerMonth());
        result.setCountry(house.getLocation().getCountry());
        result.setRegion(house.getLocation().getRegion());
        result.setStreet(house.getLocation().getStreet());
        result.setCivicNumber(house.getLocation().getCivicNumber());
        result.setState(houseAdvertisement.getState());
        result.setPublishedByCode(userAccount.getUserCode());
        result.setPublishedByImages(userAccount.getImages());
        if(result.getPublishedByImages() == null){
            result.setPublishedByImages(new ArrayList<>());
        }
        result.setPublishedByEmail(userAccount.getEmail());
        result.setPublishedByPhoneNumber(userAccount.getPhoneNumber());
        result.setDescription(houseAdvertisement.getDescription());

        return result;
    }
}
