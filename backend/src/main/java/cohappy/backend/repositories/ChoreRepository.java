package cohappy.backend.repositories;

import cohappy.backend.model.HouseChore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChoreRepository extends MongoRepository<HouseChore, String> {
    List<HouseChore> findByHouseCode(String houseCode);

    Optional<HouseChore> findByChoreCode(String choreCode);

    @Query("{ '$where': 'for (var date in this.assignedTo) { if (this.assignedTo[date] == \"?0\") return true; } return false;' }")
    List<HouseChore> findByAssignedToValue(String userCode);
}
