package cohappy.backend.it;

import cohappy.backend.exceptions.NotFoundException;
import cohappy.backend.model.Portfolio;
import cohappy.backend.model.UserAccount;
import cohappy.backend.model.dto.CreateDebtDTO;
import cohappy.backend.model.dto.MoveMoneyDTO;
import cohappy.backend.model.dto.MoveMoneyOperationEnum;
import cohappy.backend.model.dto.SendMoneyDTO;
import cohappy.backend.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;

import static cohappy.backend.model.OperationResultMessages.USER_NOT_FOUND;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Slf4j
public class PortfolioControllerIT extends BaseIT {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USER_CODE = "USR-999";
    private static final String USER_CODE_2 = "USR-999-2";
    private static final float MAX_MONEY_ACCOUNT = 1000000f;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    /* ########################################
                   Get Portafolio
     ########################################*/


    @Test
    void shouldGetPortafolio() throws Exception {
        saveDefaultUser();
        mockMvc.perform(get("/api/portafolio/" + USER_CODE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.debts").isArray());
    }

    @Test
    void shouldNotFoundGetPortafolio() throws Exception {
        saveDefaultUser();
        mockMvc.perform(get("/api/portafolio/NOTEXISTING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /* ########################################
               Add Money to Portafolio
     ########################################*/

    @Test
    void shouldAddMoneyPortafolio() throws Exception {
        saveDefaultUser();

        MoveMoneyDTO request = new MoveMoneyDTO(
                USER_CODE,
                MoveMoneyOperationEnum.SEND,
                100
        );

        mockMvc.perform(patch("/api/portafolio/money/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        UserAccount userAccount = userRepository.findByUserCode(USER_CODE)
                .orElseThrow(() -> new NotFoundException("User %s not found".formatted(USER_CODE)));

        assertThat(userAccount.getPortfolio().getAmount()).isEqualTo(200);
    }

    @Test
    void shouldFailMaxMoneyAddMoneyPortafolio() throws Exception {
        saveDefaultUser();

        MoveMoneyDTO request = new MoveMoneyDTO(
                USER_CODE,
                MoveMoneyOperationEnum.SEND,
                MAX_MONEY_ACCOUNT
        );

        mockMvc.perform(patch("/api/portafolio/money/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void shouldFailNotFoundAddMoneyPortafolio() throws Exception {
        saveDefaultUser();

        MoveMoneyDTO request = new MoveMoneyDTO(
                "NOTEXISTING",
                MoveMoneyOperationEnum.SEND,
                100
        );

        mockMvc.perform(patch("/api/portafolio/money/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailBadRequestAddMoneyPortafolio() throws Exception {
        saveDefaultUser();

        MoveMoneyDTO request = new MoveMoneyDTO(
                USER_CODE,
                MoveMoneyOperationEnum.SEND,
                0
        );

        mockMvc.perform(patch("/api/portafolio/money/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /* ########################################
           Retrieve Money to Portafolio
     ########################################*/

    @Test
    void shouldRetrieveMoneyPortafolio() throws Exception {
        saveDefaultUser();

        MoveMoneyDTO request = new MoveMoneyDTO(
                USER_CODE,
                MoveMoneyOperationEnum.RETRIEVE,
                50.5f
        );

        mockMvc.perform(patch("/api/portafolio/money/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        UserAccount userAccount = userRepository.findByUserCode(USER_CODE)
                .orElseThrow(() -> new NotFoundException("User %s not found".formatted(USER_CODE)));

        assertThat(userAccount.getPortfolio().getAmount()).isEqualTo(49.5f);
    }

    @Test
    void shouldFailNotEnoughFundRetrieveMoneyPortafolio() throws Exception {
        saveDefaultUser();

        MoveMoneyDTO request = new MoveMoneyDTO(
                USER_CODE,
                MoveMoneyOperationEnum.RETRIEVE,
                MAX_MONEY_ACCOUNT
        );

        mockMvc.perform(patch("/api/portafolio/money/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void shouldFailNotFoundRetrieveMoneyPortafolio() throws Exception {
        saveDefaultUser();

        MoveMoneyDTO request = new MoveMoneyDTO(
                "NOTEXISTING",
                MoveMoneyOperationEnum.RETRIEVE,
                100
        );

        mockMvc.perform(patch("/api/portafolio/money/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailBadRequestRetrieveMoneyPortafolio() throws Exception {
        saveDefaultUser();

        MoveMoneyDTO request = new MoveMoneyDTO(
                USER_CODE,
                MoveMoneyOperationEnum.RETRIEVE,
                0
        );

        mockMvc.perform(patch("/api/portafolio/money/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /* ########################################
           Send Money to Portafolio
     ########################################*/


    @Test
    void shouldSendMoneyPortafolio() throws Exception {
        saveDefaultUser();
        saveDefaultUser2();

        SendMoneyDTO request = new SendMoneyDTO(
                USER_CODE,
                USER_CODE_2,
                50
        );

        mockMvc.perform(patch("/api/portafolio/money/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        UserAccount userAccount = userRepository.findByUserCode(USER_CODE)
                .orElseThrow(() -> new NotFoundException("User %s not found".formatted(USER_CODE)));

        UserAccount userAccount2 = userRepository.findByUserCode(USER_CODE_2)
                .orElseThrow(() -> new NotFoundException("User %s not found".formatted(USER_CODE)));

        assertThat(userAccount.getPortfolio().getAmount()).isEqualTo(50f);
        assertThat(userAccount2.getPortfolio().getAmount()).isEqualTo(150f);
    }

    @Test
    void shouldFailSamePersonSendMoneyPortafolio() throws Exception {
        saveDefaultUser();
        saveDefaultUser2();

        SendMoneyDTO request = new SendMoneyDTO(
                USER_CODE,
                USER_CODE,
                50
        );

        mockMvc.perform(patch("/api/portafolio/money/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    //The rest of the cases are covered in the above tests, since it uses the same function of the service
    /* ########################################
           Create Debt Portafolio
     ########################################*/

    @Test
    void shouldCreateDebt() throws Exception {
        saveDefaultUser();
        saveDefaultUser2();

        CreateDebtDTO request = new CreateDebtDTO(
                USER_CODE,
                USER_CODE_2,
                50,
                ""
        );

        mockMvc.perform(post("/api/portafolio/debt/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        var debtsSender = userRepository.findByUserCode(USER_CODE)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(USER_CODE)))
                .getPortfolio()
                .getDebts();

        assertThat(debtsSender.size()).isEqualTo(1);
        assertThat(debtsSender.getFirst().getDebtorUserCode()).isEqualTo(USER_CODE);
        assertThat(debtsSender.getFirst().getBeneficiaryUserCode()).isEqualTo(USER_CODE_2);
        assertThat(debtsSender.getFirst().getAmount()).isEqualTo(50);

        var debtsReceiver = userRepository.findByUserCode(USER_CODE_2)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(USER_CODE_2)))
                .getPortfolio()
                .getDebts();

        assertThat(debtsReceiver.size()).isEqualTo(1);
        assertThat(debtsReceiver.getFirst().getDebtorUserCode()).isEqualTo(USER_CODE_2);
        assertThat(debtsReceiver.getFirst().getBeneficiaryUserCode()).isEqualTo(USER_CODE);
        assertThat(debtsReceiver.getFirst().getAmount()).isEqualTo(50);
        assertThat(debtsReceiver.getFirst().getLinkedDebtId()).isEqualTo(debtsSender.getFirst().getDebtId());
    }

    @Test
    void shouldFailNotFoundCreateDebt() throws Exception {
        saveDefaultUser();
        saveDefaultUser2();

        CreateDebtDTO request = new CreateDebtDTO(
                "NOTEXISTING",
                USER_CODE_2,
                50,
                ""
        );

        mockMvc.perform(post("/api/portafolio/debt/create")
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
                "NOTEXISTING",
                50,
                ""
        );

        mockMvc.perform(post("/api/portafolio/debt/create")
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
                USER_CODE,
                50,
                ""
        );

        mockMvc.perform(post("/api/portafolio/debt/create")
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
                USER_CODE_2,
                50,
                ""
        );
        mockMvc.perform(post("/api/portafolio/debt/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        var debtsSender = userRepository.findByUserCode(USER_CODE)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(USER_CODE)))
                .getPortfolio()
                .getDebts();

        log.info(debtsSender.getFirst().getDebtId());
        mockMvc.perform(delete("/api/portafolio/debt/delete/" + debtsSender.getFirst().getDebtId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        debtsSender = userRepository.findByUserCode(USER_CODE)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(USER_CODE)))
                .getPortfolio()
                .getDebts();
        assertThat(debtsSender.size()).isEqualTo(0);
    }

    @Test
    void shouldFailNotFoundDeleteDebt() throws Exception {
        mockMvc.perform(delete("/api/portafolio/debt/delete/NOTEXISTING")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    private UserAccount createDefaultUser() {
        UserAccount user = new UserAccount();
        user.setEmail("test@cohappy.it");
        user.setPhoneNumber("123");
        user.setPassword("secret123");
        user.setUserCode(USER_CODE);

        Portfolio portfolio = new Portfolio();
        portfolio.setDebts(new ArrayList<>());
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
