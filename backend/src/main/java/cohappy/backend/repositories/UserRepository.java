package cohappy.backend.repositories;

import cohappy.backend.model.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserAccount, String> {
    Optional<UserAccount> findByUserCode(String userCode);

    Optional<UserAccount> findByEmail(String email);

    Optional<UserAccount> findByPhoneNumber(String phoneNumber);

    List<UserAccount> findByPortfolioDebtsDebtIdIn(List<String> debtIds);

    long deleteByUserCode(String userCode);
}
