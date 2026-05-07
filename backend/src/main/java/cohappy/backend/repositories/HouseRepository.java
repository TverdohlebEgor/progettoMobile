package cohappy.backend.repositories;

import cohappy.backend.model.House;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HouseRepository extends MongoRepository<House, String> {
    Optional<House> findByHouseCode(String houseCode);

    Optional<House> findFirstByAdminsContainsOrUsersContains(String userCode, String userCodeAgain);

    default Optional<House> houseOf(String userCode){
        return findFirstByAdminsContainsOrUsersContains(userCode,userCode);
    }

    long deleteByHouseCode(String houseCode);
}
