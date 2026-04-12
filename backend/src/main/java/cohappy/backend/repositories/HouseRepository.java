package cohappy.backend.repositories;

import cohappy.backend.model.House;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HouseRepository extends MongoRepository<House, String> {
    Optional<House> findByHouseCode(String houseCode);

    long deleteByHouseCode(String houseCode);
}
