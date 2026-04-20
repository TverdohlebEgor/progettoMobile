package cohappy.backend.repositories;

import cohappy.backend.model.HouseChore;
import cohappy.backend.model.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChoreRepository extends MongoRepository<HouseChore, String> {
    List<HouseChore> findByHouseCode(String houseCode);

    Optional<HouseChore> findByChoreCode(String choreCode);
}
