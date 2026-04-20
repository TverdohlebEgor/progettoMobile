package cohappy.backend.mappers;

import cohappy.backend.model.HouseChore;
import cohappy.backend.model.UserAccount;
import cohappy.backend.model.dto.request.CreateChoreDTO;
import cohappy.backend.model.dto.response.GetChoreDTO;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChoreMapper {
    public GetChoreDTO choreToDTO(HouseChore chore, LocalDate date, UserAccount user) {
        byte[] immage = null;
        if(user.getImages() != null && !user.getImages().isEmpty()){
            immage = user.getImages().getFirst();
        }
        return new GetChoreDTO(
                chore.getChoreCode(),
                chore.getAssignedTo().get(date),
                user.getName(),
                immage,
                chore.getCompleted().get(date),
                chore.getCreatedBy(),
                chore.getHouseCode(),
                chore.getName(),
                chore.getDescription()
        );
    }

    public HouseChore createDTOtoChore(CreateChoreDTO createChoreDTO) {
        Map<LocalDate, Boolean> completedMap = createChoreDTO.getDays().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        d -> false
                ));

        return new HouseChore(
                UUID.randomUUID().toString(),
                createChoreDTO.getDays(),
                createChoreDTO.getAssignedTo(),
                completedMap,
                createChoreDTO.getCreatedBy(),
                createChoreDTO.getHouseCode(),
                createChoreDTO.getName(),
                createChoreDTO.getDescription()
        );
    }
}
