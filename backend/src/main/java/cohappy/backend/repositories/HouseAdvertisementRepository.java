package cohappy.backend.repositories;

import cohappy.backend.model.HouseAdvertisement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HouseAdvertisementRepository extends MongoRepository<HouseAdvertisement, String> {
    Optional<HouseAdvertisement> findByHouseCode(String houseCode);

    long deleteByHouseCode(String houseCode);
}
