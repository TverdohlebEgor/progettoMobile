package cohappy.backend.service;

import cohappy.backend.exceptions.IllegalInputException;
import cohappy.backend.exceptions.NoPatchException;
import cohappy.backend.exceptions.NotFoundException;
import cohappy.backend.mappers.HouseAdvertisementMapper;
import cohappy.backend.model.House;
import cohappy.backend.model.HouseAdvertisement;
import cohappy.backend.model.HouseState;
import cohappy.backend.model.UserAccount;
import cohappy.backend.model.dto.CreateHouseAdvertisementDTO;
import cohappy.backend.model.dto.GetHouseAdvertesimentDTO;
import cohappy.backend.model.dto.ModifyHouseAdvertisementDTO;
import cohappy.backend.repositories.HouseAdvertisementRepository;
import cohappy.backend.repositories.HouseRepository;
import cohappy.backend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class HouseAdvertisementService {
    private final HouseAdvertisementRepository houseAdvertisementRepository;
    private final HouseRepository houseRepository;
    private final UserRepository userRepository;
    private final HouseAdvertisementMapper mapper = new HouseAdvertisementMapper();


    public GetHouseAdvertesimentDTO getHouseAdvertisement(String houseCode) {
        Optional<HouseAdvertisement> houseAdvertisementOptional = houseAdvertisementRepository.findByHouseCode(houseCode);
        if (houseAdvertisementOptional.isEmpty()) {
            throw new NotFoundException("house advertisemenet with code %s not found".formatted(houseCode));
        }

        Optional<House> houseOptional = houseRepository.findByHouseCode(houseCode);
        if (houseOptional.isEmpty()) {
            throw new NotFoundException("house with code %s not found".formatted(houseCode));
        }

        Optional<UserAccount> userAccountOptional = userRepository.findByUserCode(houseAdvertisementOptional.get().getPublishedBy());
        if (userAccountOptional.isEmpty()) {
            throw new NotFoundException("user with code %s not found".formatted(houseAdvertisementOptional.get().getPublishedBy()));
        }

        return mapper.houseAdvertesimentToGetDTO(
                houseAdvertisementOptional.get(),
                houseOptional.get(),
                userAccountOptional.get()
        );

    }

    public List<GetHouseAdvertesimentDTO> getAllHouseAdvertisement() {

        List<HouseAdvertisement> houseAdvertisements = houseAdvertisementRepository.findAll();
        List<GetHouseAdvertesimentDTO> result = new ArrayList<>();

        for(HouseAdvertisement houseAdvertisement : houseAdvertisements){

            if(houseAdvertisement.getState() == HouseState.PRIVATE){
                continue;
            }

            Optional<House> houseOptional = houseRepository.findByHouseCode(houseAdvertisement.getHouseCode());
            if (houseOptional.isEmpty()) {
                throw new NotFoundException("house with code %s not found".formatted(houseAdvertisement.getHouseCode()));
            }

            Optional<UserAccount> userAccountOptional = userRepository.findByUserCode(houseAdvertisement.getPublishedBy());
            if (userAccountOptional.isEmpty()) {
                throw new NotFoundException("user with code %s not found".formatted(houseAdvertisement.getPublishedBy()));
            }

            result.add(mapper.houseAdvertesimentToGetDTO(
                    houseAdvertisement ,
                    houseOptional.get(),
                    userAccountOptional.get()
            ));
        }

        return result;
    }

    public boolean deleteHouseAdvertisement(String houseCode) {
        return houseAdvertisementRepository.deleteByHouseCode(houseCode) > 0;
    }

    public void modifyHouseAdvertisement(ModifyHouseAdvertisementDTO modifyHouseAdvertisementDTO) {
        String houseCode = modifyHouseAdvertisementDTO.getHouseCode();
        if (houseCode == null) {
            throw new IllegalInputException("House code can't be null");
        }

        HouseAdvertisement houseAdvertisement = houseAdvertisementRepository.findByHouseCode(houseCode)
                .orElseThrow(
                        () -> new NotFoundException("house advertisemenet with code %s not found".formatted(houseCode))
                );

        if (modifyHouseAdvertisementDTO.getState() == null) {
            throw new IllegalInputException("state value can't be null");
        }

        if (houseAdvertisement.getState() == modifyHouseAdvertisementDTO.getState()) {
            throw new NoPatchException("The state was already the one given");
        }

        if(modifyHouseAdvertisementDTO.getDescription() != null){
            houseAdvertisement.setDescription(modifyHouseAdvertisementDTO.getDescription());
        }
        houseAdvertisement.setState(modifyHouseAdvertisementDTO.getState());
        houseAdvertisementRepository.save(houseAdvertisement);
    }

    public void createHouseAdvertisement(CreateHouseAdvertisementDTO createHouseAdvertisementDTO) {
        String houseCode = createHouseAdvertisementDTO.getHouseCode();
        if (houseCode == null) {
            throw new IllegalInputException("House code can't be null");
        }

        Optional<House> houseOptional = houseRepository.findByHouseCode(houseCode);
        if(houseOptional.isEmpty()){
            throw new NotFoundException("The house with code %s has not been found".formatted(houseCode));
        }

        Optional<HouseAdvertisement> houseAdvertisementOptional = houseAdvertisementRepository.findByHouseCode(houseCode);
        if(houseAdvertisementOptional.isPresent()){
            throw new IllegalInputException("The house code %s is already connected to an advertisement".formatted(houseCode));
        }

        HouseAdvertisement result = new HouseAdvertisement();
        result.setHouseCode(houseCode);
        result.setDescription(createHouseAdvertisementDTO.getDescription());
        result.setState(createHouseAdvertisementDTO.getState());
        if(result.getState() == null){
            result.setState(HouseState.PUBLIC);
        }

        String userCode = createHouseAdvertisementDTO.getPublishedBy();
        if(userCode == null){
            throw new IllegalInputException("Published by is a mandatory value");
        }
        Optional<UserAccount> userOptional = userRepository.findByUserCode(userCode);
        if(userOptional.isEmpty()){
            throw new NotFoundException("The user with code %s has not been found".formatted(userCode));
        }

        result.setPublishedBy(createHouseAdvertisementDTO.getPublishedBy());
        houseAdvertisementRepository.save(result);
    }
}
