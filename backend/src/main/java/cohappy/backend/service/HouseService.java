package cohappy.backend.service;

import cohappy.backend.exceptions.IllegalInputException;
import cohappy.backend.exceptions.NoPatchException;
import cohappy.backend.exceptions.NotFoundException;
import cohappy.backend.mappers.HouseMapper;
import cohappy.backend.model.House;
import cohappy.backend.model.dto.*;
import cohappy.backend.repositories.HouseRepository;
import cohappy.backend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static cohappy.backend.util.StringCheckUtil.isEmptyString;

@AllArgsConstructor
@Service
public class HouseService {
    private final HouseRepository houseRepository;
    private final UserRepository userRepository;
    private final HouseMapper mapper = new HouseMapper();

    public GetHouseDTO getHouse(String houseCode) {
        Optional<House> houseOptional = houseRepository.findByHouseCode(houseCode);
        if (houseOptional.isEmpty()) {
            throw new NotFoundException("house with code %s not found".formatted(houseCode));
        }
        return mapper.houseToGetDTO(houseOptional.get());
    }

    public boolean deleteHouse(String houseCode) {
        return houseRepository.deleteByHouseCode(houseCode) > 0;
    }

    public void modifyHouse(ModifyHouseDTO modifyHouseDTO) {
        if (modifyHouseDTO.getHouseCode() == null) {
            throw new IllegalInputException("House code is an mandatory value");
        }
        if (modifyHouseDTO.areAllNull()) {
            throw new IllegalInputException("At least one value of the request must be not null");
        }

        House house = houseRepository.findByHouseCode(modifyHouseDTO.getHouseCode())
                .orElseThrow(
                        () -> new NotFoundException("house with code %s not found".formatted(modifyHouseDTO.getHouseCode()))
                );


        House newHouse = house.copy();

        if (modifyHouseDTO.getImages() != null) {
            newHouse.setImages(modifyHouseDTO.getImages());
        }
        if (modifyHouseDTO.getCostPerMonth() != null) {
            newHouse.setCostPerMonth(modifyHouseDTO.getCostPerMonth());
        }
        if (modifyHouseDTO.getCountry() != null) {
            newHouse.getLocation().setCountry(modifyHouseDTO.getCountry());
        }
        if (modifyHouseDTO.getRegion() != null) {
            newHouse.getLocation().setRegion(modifyHouseDTO.getRegion());
        }
        if (modifyHouseDTO.getStreet() != null) {
            newHouse.getLocation().setStreet(modifyHouseDTO.getStreet());
        }
        if (modifyHouseDTO.getCivicNumber() != null) {
            newHouse.getLocation().setCivicNumber(modifyHouseDTO.getCivicNumber());
        }

        if (newHouse.isEqualTo(house)) {
            throw new NoPatchException("The value given were already set in the house");
        }

        houseRepository.save(newHouse);
    }

    public String createHouse(CreateHouseDTO createHouseDTO) {
        String userCode = createHouseDTO.getUserCode();
        validateInput(userCode, "User Code");
        validateInput(createHouseDTO.getCountry(), "Country");
        validateInput(createHouseDTO.getRegion(), "Region");
        validateInput(createHouseDTO.getStreet(), "Street");
        if (createHouseDTO.getCostPerMonth() == null) {
            throw new IllegalInputException("the value costPerMonth must be not null");
        }
        if (createHouseDTO.getCivicNumber() == null) {
            throw new IllegalInputException("the value civicNumber must be not null");
        }

        userRepository.findByUserCode(createHouseDTO.getUserCode())
                .orElseThrow(
                        () -> new NotFoundException("The user with code %s has not been found".formatted(userCode))
                );

        House newHouse = mapper.CreateDTOToHouse(createHouseDTO);
        houseRepository.save(newHouse);
        return newHouse.getHouseCode();
    }


    public void addAdmin(AddAdminDTO addAdminDTO) {
        validateInput(addAdminDTO.getHouseCode(), "House code");
        validateInput(addAdminDTO.getUserCode(), "User code");

        String houseCode = addAdminDTO.getHouseCode();
        String userCode = addAdminDTO.getUserCode();

        House house = houseRepository.findByHouseCode(houseCode)
                .orElseThrow(
                        () -> new NotFoundException("house with code %s not found".formatted(houseCode))
                );

        userRepository.findByUserCode(userCode)
                .orElseThrow(
                        () -> new NotFoundException("The user with code %s has not been found".formatted(userCode))
                );

        if(house.getAdmins().contains(userCode)){
            throw new IllegalInputException("The user %s is already an admin".formatted(userCode));
        }

        if(!house.getUsers().contains(userCode)){
            throw new IllegalInputException("The user %s is not a user of the house %s".formatted(userCode,houseCode));
        }

        house.getUsers().remove(userCode);
        house.getAdmins().add(userCode);

        houseRepository.save(house);
    }

    public void removeAdmin(RemoveAdminDTO RemoveAdminDTO) {
        validateInput(RemoveAdminDTO.getHouseCode(), "House code");
        validateInput(RemoveAdminDTO.getUserCode(), "User code");

        String houseCode = RemoveAdminDTO.getHouseCode();
        String userCode = RemoveAdminDTO.getUserCode();

        House house = houseRepository.findByHouseCode(houseCode)
                .orElseThrow(
                        () -> new NotFoundException("house with code %s not found".formatted(houseCode))
                );

        userRepository.findByUserCode(userCode)
                .orElseThrow(
                        () -> new NotFoundException("The user with code %s has not been found".formatted(userCode))
                );

        if(!house.getAdmins().contains(userCode)){
            throw new IllegalInputException("The user %s is not an admin of house %s".formatted(userCode,houseCode));
        }

        if(house.getAdmins().size() == 1){
            throw new IllegalInputException("The user %s is the only admin, make someone else admin before removing it".formatted(userCode));
        }

        house.getAdmins().remove(userCode);

        houseRepository.save(house);
    }

    public void addUser(AddUserDTO addUserDTO) {
        validateInput(addUserDTO.getHouseCode(), "House code");
        validateInput(addUserDTO.getUserCode(), "User code");

        String houseCode = addUserDTO.getHouseCode();
        String userCode = addUserDTO.getUserCode();

        House house = houseRepository.findByHouseCode(houseCode)
                .orElseThrow(
                        () -> new NotFoundException("house with code %s not found".formatted(houseCode))
                );

        userRepository.findByUserCode(userCode)
                .orElseThrow(
                        () -> new NotFoundException("The user with code %s has not been found".formatted(userCode))
                );

        if(house.getUsers().contains(userCode)){
            throw new IllegalInputException("The user %s is already a user of the house %s".formatted(userCode,houseCode));
        }

        if(house.getAdmins().contains(userCode)){
            throw new IllegalInputException("The user %s is already an admin of the house %s".formatted(userCode,houseCode));
        }

        house.getUsers().add(userCode);
        houseRepository.save(house);
    }

    public void removeUser(RemoveUserDTO removeUserDTO) {
        validateInput(removeUserDTO.getHouseCode(), "House code");
        validateInput(removeUserDTO.getUserCode(), "User code");

        String houseCode = removeUserDTO.getHouseCode();
        String userCode = removeUserDTO.getUserCode();

        House house = houseRepository.findByHouseCode(houseCode)
                .orElseThrow(
                        () -> new NotFoundException("house with code %s not found".formatted(houseCode))
                );

        userRepository.findByUserCode(userCode)
                .orElseThrow(
                        () -> new NotFoundException("The user with code %s has not been found".formatted(userCode))
                );

        if(!house.getUsers().contains(userCode)){
            throw new IllegalInputException("The user %s is not a user of the house %s".formatted(userCode,houseCode));
        }

        house.getUsers().remove(userCode);
        houseRepository.save(house);
    }

    private void validateInput(String str, String fieldName) {
        if (isEmptyString(str)) {
            throw new IllegalInputException("the value %s must be filled and not empty".formatted(fieldName));
        }
    }

}
