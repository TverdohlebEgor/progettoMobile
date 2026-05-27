package cohappy.backend.it;

import cohappy.backend.model.Debt;
import cohappy.backend.repositories.DebtRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class DebtRepositoryIT extends BaseIT {
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @Autowired
    private DebtRepository debtRepository;

    @AfterEach
    void cleanUp() {
        debtRepository.deleteAll();
    }

    @Test
    void shouldFindDebtWhenUserCodeExistsInDebtorsCodeMap() {
        Debt debt1 = new Debt();
        Map<String, Boolean> map1 = new HashMap<>();
        map1.put("USER_123", true);
        debt1.setDebtorsCode(map1);
        debt1.setDescription("Testcontainers debt");

        debtRepository.save(debt1);

        List<Debt> results = debtRepository.findByUserCodeInDebtors("USER_123");

        assertThat(results)
            .hasSize(1)
            .first()
            .satisfies(debt -> {
                assertThat(debt.getDescription()).isEqualTo("Testcontainers debt");
                assertThat(debt.getDebtorsCode()).containsKey("USER_123");
            });
    }
}