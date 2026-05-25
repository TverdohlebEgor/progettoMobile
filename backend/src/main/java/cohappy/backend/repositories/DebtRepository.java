package cohappy.backend.repositories;

import cohappy.backend.model.Debt;
import cohappy.backend.model.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DebtRepository extends MongoRepository<Debt, String> {
    Optional<Debt> findByDebtId(String debtId);

    long deleteByDebtId(String userCode);

    List<Debt> findByCreditorUserCode(String userCode);
    
    @Query("{ 'debtorsCode.?0' : { $exists: true } }")
    List<Debt> findByUserCodeInDebtors(String userCode);
}
