package cohappy.backend.service;

import cohappy.backend.exceptions.IllegalInputException;
import cohappy.backend.exceptions.NoPatchException;
import cohappy.backend.exceptions.NotFoundException;
import cohappy.backend.mappers.ChoreMapper;
import cohappy.backend.model.HouseChore;
import cohappy.backend.model.UserAccount;
import cohappy.backend.model.dto.request.CreateChoreDTO;
import cohappy.backend.model.dto.request.PatchChoreDTO;
import cohappy.backend.model.dto.response.GetChoreDTO;
import cohappy.backend.repositories.ChoreRepository;
import cohappy.backend.repositories.HouseRepository;
import cohappy.backend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;

import static cohappy.backend.model.OperationResultMessages.*;

@AllArgsConstructor
@Service
public class ChoreService {

    private final ChoreRepository choreRepository;
    private final UserRepository userRepository;
    private final HouseRepository houseRepository;

    private final ChoreMapper mapper = new ChoreMapper();

    public List<GetChoreDTO> getChore(String houseCode, LocalDate date) {
        List<HouseChore> chores = choreRepository.findByHouseCode(houseCode);

        return chores.stream().filter(c -> c.getDays().contains(date))
                .map(c -> {
                    String userCode = c.getAssignedTo().get(date);
                    if(userCode == null){
                       throw new IllegalInputException("A day of the chore has not assigned user");
                    }
                    UserAccount asignedTo = userRepository.findByUserCode(userCode)
                            .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(userCode)));
                    return mapper.choreToDTO(c, date, asignedTo);
                }).toList();
    }

    public void patchChore(@RequestBody PatchChoreDTO patchChoreDTO) {
        if(patchChoreDTO.getChoreCode() == null){
            throw new IllegalInputException(INVALID_INPUT.formatted("chore code"));
        }

        HouseChore chore = choreRepository.findByChoreCode(patchChoreDTO.getChoreCode())
                .orElseThrow(() -> new NotFoundException(CHORE_NOT_FOUND.formatted(patchChoreDTO.getChoreCode())));

        LocalDate day = patchChoreDTO.getDay();

        if (!chore.getDays().contains(day)) {
            throw new IllegalInputException("Chore: %s is not present on day: %s".formatted(patchChoreDTO.getChoreCode(), day));
        }

        boolean modified = false;

        if (patchChoreDTO.getAssignedTo() != null && !patchChoreDTO.getAssignedTo().equals(chore.getAssignedTo().get(day))) {
            chore.getAssignedTo().put(day, patchChoreDTO.getAssignedTo());
            modified = true;
        }

        if (patchChoreDTO.getCompleted() != null && patchChoreDTO.getCompleted() != chore.getCompleted().get(day)) {
            chore.getCompleted().put(day, patchChoreDTO.getCompleted());
            modified = true;
        }

        if (patchChoreDTO.getHouseCode() != null && patchChoreDTO.getCompleted() != chore.getCompleted().get(day)) {
            chore.getCompleted().put(day, patchChoreDTO.getCompleted());
            modified = true;
        }

        if (patchChoreDTO.getName() != null && !patchChoreDTO.getName().equals(chore.getName())) {
            chore.setName(patchChoreDTO.getName());
            modified = true;
        }

        if (patchChoreDTO.getDescription() != null && !patchChoreDTO.getDescription().equals(chore.getDescription())) {
            chore.setDescription(patchChoreDTO.getDescription());
            modified = true;
        }

        if (!modified) {
            throw new NoPatchException(NO_PATCH.formatted("Chore " + patchChoreDTO.getChoreCode()));
        }

        choreRepository.save(chore);
    }

    public void deleteChore(String choreCode) {
        HouseChore chore = choreRepository.findByChoreCode(choreCode)
                .orElseThrow(() -> new NotFoundException(CHORE_NOT_FOUND.formatted(choreCode)));

        choreRepository.delete(chore);
    }

    public void createChore(CreateChoreDTO createChoreDTO) {
        if(createChoreDTO.getHouseCode() == null){
            throw new IllegalInputException(INVALID_INPUT.formatted("house code"));
        }
        houseRepository.findByHouseCode(createChoreDTO.getHouseCode())
                .orElseThrow(() -> new NotFoundException(HOUSE_NOT_FOUND.formatted(createChoreDTO.getHouseCode())));

        if (createChoreDTO.getDays().isEmpty()) {
            throw new IllegalInputException("Can't create chore with empty day list");
        }

        for (LocalDate day : createChoreDTO.getDays()) {
            if (!createChoreDTO.getAssignedTo().containsKey(day)) {
                throw new IllegalInputException("A day of the chore has not assigned user");
            }

            String userCode = createChoreDTO.getAssignedTo().get(day);
            userRepository.findByUserCode(userCode)
                    .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(userCode)));
        }

        HouseChore newChore = mapper.createDTOtoChore(createChoreDTO);

        choreRepository.save(newChore);
    }

}
