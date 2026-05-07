package cohappy.backend.service;

import cohappy.backend.exceptions.IllegalInputException;
import cohappy.backend.exceptions.NoPatchException;
import cohappy.backend.exceptions.NotFoundException;
import cohappy.backend.mappers.HouseAdvertisementMapper;
import cohappy.backend.model.House;
import cohappy.backend.model.HouseAdvertisement;
import cohappy.backend.model.HouseState;
import cohappy.backend.model.UserAccount;
import cohappy.backend.model.dto.request.CreateHouseAdvertisementDTO;
import cohappy.backend.model.dto.request.ModifyHouseAdvertisementDTO;
import cohappy.backend.model.dto.response.GetHouseAdvertesimentDTO;
import cohappy.backend.repositories.HouseAdvertisementRepository;
import cohappy.backend.repositories.HouseRepository;
import cohappy.backend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static cohappy.backend.model.OperationResultMessages.*;

@Slf4j
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
            throw new NotFoundException(HOUSE_ADVERTISEMENT_NOT_FOUND.formatted(houseCode));
        }

        Optional<House> houseOptional = houseRepository.findByHouseCode(houseCode);
        if (houseOptional.isEmpty()) {
            throw new NotFoundException(HOUSE_NOT_FOUND.formatted(houseCode));
        }

        Optional<UserAccount> userAccountOptional = userRepository.findByUserCode(houseAdvertisementOptional.get().getPublishedBy());
        if (userAccountOptional.isEmpty()) {
            throw new NotFoundException(USER_NOT_FOUND.formatted(houseAdvertisementOptional.get().getPublishedBy()));
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

        for (HouseAdvertisement houseAdvertisement : houseAdvertisements) {

            if (houseAdvertisement.getState() == HouseState.PRIVATE) {
                continue;
            }

            Optional<House> houseOptional = houseRepository.findByHouseCode(houseAdvertisement.getHouseCode());
            if (houseOptional.isEmpty()) {
                throw new NotFoundException(HOUSE_NOT_FOUND.formatted(houseAdvertisement.getHouseCode()));
            }

            Optional<UserAccount> userAccountOptional = userRepository.findByUserCode(houseAdvertisement.getPublishedBy());
            if (userAccountOptional.isEmpty()) {
                throw new NotFoundException(USER_NOT_FOUND.formatted(houseAdvertisement.getPublishedBy()));
            }

            result.add(mapper.houseAdvertesimentToGetDTO(
                    houseAdvertisement,
                    houseOptional.get(),
                    userAccountOptional.get()
            ));
        }

        return result;
    }

    public boolean deleteHouseAdvertisement(String houseCode) {
        return houseAdvertisementRepository.deleteByHouseCode(houseCode) > 0;
    }

    public void modifyHouseAdvertisement(ModifyHouseAdvertisementDTO dto) {
    validateModifyDto(dto);

    HouseAdvertisement adv = houseAdvertisementRepository.findByHouseCode(dto.getHouseCode())
            .orElseThrow(() -> new NotFoundException(HOUSE_ADVERTISEMENT_NOT_FOUND.formatted(dto.getHouseCode())));

    boolean modified = false;

    modified |= updateIfChanged(dto.getDescription(), adv.getDescription(), adv::setDescription);
    modified |= updateIfChanged(mapper.DTOToHouseState(dto.getState()), adv.getState(), adv::setState);

    List<byte[]> newImages = Optional.ofNullable(dto.getImmages()).orElseGet(ArrayList::new);
    modified |= updateIfChanged(newImages, adv.getImmages(), adv::setImmages);

    if (!modified) {
        throw new NoPatchException(NO_PATCH.formatted("House Advertisement"));
    }

    houseAdvertisementRepository.save(adv);
}

    public void createHouseAdvertisement(CreateHouseAdvertisementDTO createHouseAdvertisementDTO) {
        String houseCode = createHouseAdvertisementDTO.getHouseCode();
        if (houseCode == null) {
            throw new IllegalInputException(INVALID_INPUT.formatted("HouseCode"));
        }

        Optional<House> houseOptional = houseRepository.findByHouseCode(houseCode);
        if (houseOptional.isEmpty()) {
            throw new NotFoundException(HOUSE_NOT_FOUND.formatted(houseCode));
        }

        Optional<HouseAdvertisement> houseAdvertisementOptional = houseAdvertisementRepository.findByHouseCode(houseCode);
        if (houseAdvertisementOptional.isPresent()) {
            throw new IllegalInputException(HOUSE_HAS_ALREADY_AN_ADVERTISEMENT.formatted(houseCode));
        }

        HouseAdvertisement result = new HouseAdvertisement();
        result.setHouseCode(houseCode);
        result.setImmages(createHouseAdvertisementDTO.getImages());
        if(result.getImmages() == null){
            result.setImmages(new ArrayList<>());
        }
        result.setDescription(createHouseAdvertisementDTO.getDescription());
        result.setState(mapper.DTOToHouseState(createHouseAdvertisementDTO.getState()));
        if (result.getState() == null) {
            result.setState(HouseState.PUBLIC);
        }

        String userCode = createHouseAdvertisementDTO.getPublishedBy();
        if (userCode == null) {
            throw new IllegalInputException(INVALID_INPUT.formatted("Published"));
        }
        Optional<UserAccount> userOptional = userRepository.findByUserCode(userCode);
        if (userOptional.isEmpty()) {
            throw new NotFoundException(USER_NOT_FOUND.formatted(userCode));
        }

        result.setPublishedBy(createHouseAdvertisementDTO.getPublishedBy());
        houseAdvertisementRepository.save(result);
    }

    private <T> boolean updateIfChanged(T newValue, T currentValue, Consumer<T> setter) {
        if (newValue != null && !Objects.equals(newValue, currentValue)) {
            setter.accept(newValue);
            return true;
        }
        return false;
    }

    private void validateModifyDto(ModifyHouseAdvertisementDTO dto) {
        if (dto.getHouseCode() == null) throw new IllegalInputException(INVALID_INPUT.formatted("HouseCode"));
        if (dto.getState() == null) throw new IllegalInputException(INVALID_INPUT.formatted("State"));
    }
}
