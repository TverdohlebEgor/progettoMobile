package cohappy.backend.it;

import cohappy.backend.model.DebtType;
import cohappy.backend.model.Portfolio;
import cohappy.backend.model.UserAccount;
import cohappy.backend.model.dto.request.CreateDebtDTO;
import cohappy.backend.repositories.DebtRepository;
import cohappy.backend.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Slf4j
public class DebtControllerIT extends BaseIT {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DebtRepository debtRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USER_CODE = "USR-999";
    private static final String USER_CODE_2 = "USR-999-2";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        debtRepository.deleteAll();
    }

    /* ########################################
           Create Debt Portafolio
     ########################################*/

    @Test
    void shouldCreateDebt() throws Exception {
        saveDefaultUser();
        saveDefaultUser2();

        CreateDebtDTO request = new CreateDebtDTO(
                USER_CODE,
                Map.of(USER_CODE_2, Boolean.FALSE),
                Boolean.FALSE,
                50,
                "",
                DebtType.OTHER
        );

        mockMvc.perform(post("/api/debts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        var debts = debtRepository.findAll();

        assertThat(debts.size()).isEqualTo(1);
        assertThat(debts.getFirst().getCreditorUserCode()).isEqualTo(USER_CODE);
        assertThat(debts.getFirst().getDebtorsCode().containsKey(USER_CODE_2)).isTrue();
        assertThat(debts.getFirst().getAmount()).isEqualTo(50);
    }

    @Test
    void shouldFailNotFoundCreateDebt() throws Exception {
        saveDefaultUser();
        saveDefaultUser2();

        CreateDebtDTO request = new CreateDebtDTO(
                "NOTEXISTING",
                Map.of(USER_CODE_2, Boolean.FALSE),
                Boolean.FALSE,
                50,
                "",
                DebtType.DELIVERY_AND_EATING_OUT
        );

        mockMvc.perform(post("/api/debts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailNotFound2CreateDebt() throws Exception {
        saveDefaultUser();
        saveDefaultUser2();

        CreateDebtDTO request = new CreateDebtDTO(
                USER_CODE,
                Map.of("NOTEXISTING", Boolean.TRUE),
                Boolean.TRUE,
                50,
                "",
                DebtType.DELIVERY_AND_EATING_OUT
        );

        mockMvc.perform(post("/api/debts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailSamePersonCreateDebt() throws Exception {
        saveDefaultUser();
        saveDefaultUser2();

        CreateDebtDTO request = new CreateDebtDTO(
                USER_CODE,
                Map.of(USER_CODE, Boolean.TRUE),
                Boolean.TRUE,
                50,
                "",
                DebtType.OTHER
        );

        mockMvc.perform(post("/api/debts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /* ########################################
              Delete Debt Portafolio
     ########################################*/

    @Test
    void shouldDeleteDebt() throws Exception {
        saveDefaultUser();
        saveDefaultUser2();

        CreateDebtDTO request = new CreateDebtDTO(
                USER_CODE,
                Map.of(USER_CODE_2, Boolean.TRUE),
                Boolean.TRUE,
                50,
                "",
                DebtType.OTHER
        );
        mockMvc.perform(post("/api/debts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        var debts = debtRepository.findAll();
        assertThat(debts.size()).isEqualTo(1);

        mockMvc.perform(delete("/api/debts/delete/" + debts.getFirst().getDebtId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        debts = debtRepository.findAll();
        assertThat(debts.size()).isEqualTo(0);
    }

    @Test
    void shouldFailNotFoundDeleteDebt() throws Exception {
        mockMvc.perform(delete("/api/debts/delete/NOTEXISTING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /* ########################################
                  Get all debts
     ########################################*/
    @Test
    void shouldGetAllDebts() throws Exception {
        saveDefaultUser();
        saveDefaultUser2();

        CreateDebtDTO request = new CreateDebtDTO(
                USER_CODE,
                Map.of(USER_CODE_2, Boolean.TRUE),
                Boolean.TRUE,
                50,
                "",
                DebtType.OTHER
        );
        mockMvc.perform(post("/api/debts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/debts/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    /* ########################################
                Get total debts
     ########################################*/

    @Test
    void shouldGetUserTotalDebt() throws Exception {
        saveDefaultUser();
        saveDefaultUser2();

        CreateDebtDTO request = new CreateDebtDTO(
                USER_CODE,
                Map.of(USER_CODE_2, Boolean.TRUE),
                Boolean.TRUE,
                50,
                "",
                DebtType.OTHER
        );
        mockMvc.perform(post("/api/debts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/debts/" + USER_CODE_2 + "/total")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(50));
    }

    /* ########################################
              Get total credits
   ########################################*/
    @Test
    void shouldGetUserTotalCredits() throws Exception {
        saveDefaultUser();
        saveDefaultUser2();

        CreateDebtDTO request = new CreateDebtDTO(
                USER_CODE,
                Map.of(USER_CODE_2, Boolean.TRUE),
                Boolean.TRUE,
                50,
                "",
                DebtType.OTHER
        );
        mockMvc.perform(post("/api/debts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/debts/credits/" + USER_CODE + "/total")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(50));
    }


    private UserAccount createDefaultUser() {
        UserAccount user = new UserAccount();
        user.setEmail("test@cohappy.it");
        user.setPhoneNumber("123");
        user.setPassword("secret123");
        user.setUserCode(USER_CODE);

        Portfolio portfolio = new Portfolio();
        portfolio.setAmount(100);
        user.setPortfolio(portfolio);

        return user;
    }

    private UserAccount createDefaultUser2() {
        UserAccount user = createDefaultUser();
        user.setEmail("t@c2.it");
        user.setPhoneNumber("456");
        user.setUserCode(USER_CODE_2);

        return user;
    }

    private void saveDefaultUser() {
        userRepository.save(createDefaultUser());
    }

    private void saveDefaultUser2() {
        userRepository.save(createDefaultUser2());
    }
}
