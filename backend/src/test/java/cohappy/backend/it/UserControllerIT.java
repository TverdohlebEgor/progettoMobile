package cohappy.backend.it;

import cohappy.backend.exceptions.NotFoundException;
import cohappy.backend.model.Portfolio;
import cohappy.backend.model.UserAccount;
import cohappy.backend.model.dto.request.LoginDTO;
import cohappy.backend.model.dto.request.PatchUserDTO;
import cohappy.backend.model.dto.request.RegisterDTO;
import cohappy.backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static cohappy.backend.model.OperationResultMessages.USER_NOT_FOUND;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.setRemoveAssertJRelatedElementsFromStackTrace;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class UserControllerIT extends BaseIT{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldGetProfile() throws Exception {
        saveDefaultUser();
        mockMvc.perform(get("/api/user/profile/USR-999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userCode").value("USR-999"))
                .andExpect(jsonPath("$.email").value("test@cohappy.it"));
    }

    @Test
    void shouldNotFoundGetProfile() throws Exception {
        saveDefaultUser();
        mockMvc.perform(get("/api/user/profile/NOTEXISTING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteProfile() throws Exception {
        saveDefaultUser();
        Optional<UserAccount> account = userRepository.findByUserCode("USR-999");
        assertThat(account).isPresent();
        mockMvc.perform(delete("/api/user/delete/USR-999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        account = userRepository.findByUserCode("USR-999");
        assertThat(account).isNotPresent();
    }

    @Test
    void shouldNotFoundDeleteProfile() throws Exception {
        saveDefaultUser();
        Optional<UserAccount> account = userRepository.findByUserCode("USR-999");
        assertThat(account).isPresent();
        mockMvc.perform(delete("/api/user/delete/NOTEXISTING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        account = userRepository.findByUserCode("USR-999");
        assertThat(account).isPresent();
    }

    @Test
    void shouldLoginWithEmail() throws Exception {
        saveDefaultUser();

        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setEmail("test@cohappy.it");
        loginRequest.setPassword("secret123");

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("USR-999"));
    }

    @Test
    void shouldLoginWithPhoneNumber() throws Exception {
        saveDefaultUser();

        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setPhoneNumber("123");
        loginRequest.setPassword("secret123");

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("USR-999"));
    }

    @Test
    void shouldFailLoginForPassword() throws Exception {
        saveDefaultUser();

        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setPhoneNumber("123");
        loginRequest.setPassword("   ");

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        loginRequest.setPassword(null);
        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailLoginForEmptyEmailAndPhoneNumber() throws Exception {
        saveDefaultUser();

        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setPhoneNumber("");
        loginRequest.setEmail("  ");

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        loginRequest.setEmail(null);
        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        loginRequest.setPhoneNumber(null);
        loginRequest.setEmail("");
        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        loginRequest.setPhoneNumber(null);
        loginRequest.setEmail(null);
        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailLoginNotFound() throws Exception {
        saveDefaultUser();

        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setPhoneNumber("NOTEXISTING");
        loginRequest.setPassword("secret123");

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound());

        loginRequest.setEmail("NOTEXISTING");
        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRegister() throws Exception {
        UserAccount newUser = createDefaultUser();

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName(newUser.getName());
        registerDTO.setSurname(newUser.getSurname());
        registerDTO.setBirthDate(newUser.getBirthDate().toString());
        registerDTO.setImages(new ArrayList<>());
        registerDTO.setEmail(newUser.getEmail());
        registerDTO.setPhoneNumber(newUser.getPhoneNumber());
        registerDTO.setPassword(newUser.getPassword());

        AtomicReference<String> userCode = new AtomicReference<>();
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andDo(result -> userCode.set(result.getResponse().getContentAsString()));

        Optional<UserAccount> findedUserAccount = userRepository.findByUserCode(userCode.get());
        assertThat(findedUserAccount).isPresent();
        assertThat(findedUserAccount.get().getImages().size()).isZero();
        assertThat(findedUserAccount.get().getEmail()).isEqualTo(newUser.getEmail());
        assertThat(findedUserAccount.get().getPhoneNumber()).isEqualTo(newUser.getPhoneNumber());
        assertThat(findedUserAccount.get().getPassword()).isEqualTo(newUser.getPassword());
        assertThat(findedUserAccount.get().getPortfolio().getAmount()).isZero();
        assertThat(findedUserAccount.get().getPortfolio().getDebts().size()).isZero();
    }

    @Test
    void shouldFailRegisterForEmail() throws Exception {
        saveDefaultUser();

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setImages(new ArrayList<>());
        registerDTO.setEmail("  ");
        registerDTO.setPhoneNumber("123");
        registerDTO.setPassword("secret123");

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());

        registerDTO.setEmail(null);
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailRegisterForPhoneNumber() throws Exception {
        saveDefaultUser();

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setImages(new ArrayList<>());
        registerDTO.setEmail("test@cohappy.it");
        registerDTO.setPhoneNumber(" ");
        registerDTO.setPassword("secret123");

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());

        registerDTO.setPhoneNumber(null);
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailRegisterForPassword() throws Exception {
        saveDefaultUser();

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setImages(new ArrayList<>());
        registerDTO.setEmail("test@cohappy.it");
        registerDTO.setPhoneNumber("123");
        registerDTO.setPassword("  ");

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());

        registerDTO.setPassword(null);
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailRegisterForAlreadyUsedEmail() throws Exception {
        saveDefaultUser();

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setImages(new ArrayList<>());
        registerDTO.setEmail("test@cohappy.it");
        registerDTO.setPhoneNumber("123");
        registerDTO.setPassword("secret123");

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());

        registerDTO.setPhoneNumber("456");
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailRegisterForAlreadyUsedPhoneNumber() throws Exception {
        saveDefaultUser();

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setImages(new ArrayList<>());
        registerDTO.setEmail("test@cohappy.it");
        registerDTO.setPhoneNumber("123");
        registerDTO.setPassword("secret123");

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());

        registerDTO.setEmail("test@cohappy2.it");
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldPatchUserProfile() throws Exception {
        saveDefaultUser();

        PatchUserDTO request = new PatchUserDTO();
        request.setUserCode("USR-999");
        request.setName("Luigi");
        request.setAge(35);
        request.setPhoneNumber(null);

        mockMvc.perform(patch("/api/user/patch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        UserAccount account = userRepository.findByUserCode("USR-999")
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted("USR-999")));

        assertThat(account.getName()).isEqualTo("Luigi");
        assertThat(account.getAge()).isEqualTo(35);
        assertThat(account.getPhoneNumber()).isEqualTo("123");
    }

    @Test
    void shouldNotFoundPatchUserProfile() throws Exception {
        saveDefaultUser();

        PatchUserDTO request = new PatchUserDTO();
        request.setUserCode("NOTEXISTING");
        request.setName("Luigi");

        mockMvc.perform(patch("/api/user/patch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldBadRequestNoUserCodePatchUserProfile() throws Exception {
        saveDefaultUser();

        PatchUserDTO request = new PatchUserDTO();
        request.setUserCode(null);
        request.setName("Luigi");

        mockMvc.perform(patch("/api/user/patch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNoPatchUserProfile() throws Exception {
        UserAccount defaultUser = createDefaultUser();
        saveDefaultUser();

        PatchUserDTO request = new PatchUserDTO();
        request.setUserCode(defaultUser.getUserCode());
        request.setName(defaultUser.getName());
        mockMvc.perform(patch("/api/user/patch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    private UserAccount createDefaultUser(){
        UserAccount user = new UserAccount();
        user.setName("testName");
        user.setSurname("testSurname");
        user.setBirthDate(LocalDate.of(1999,10,10));
        user.setEmail("test@cohappy.it");
        user.setPhoneNumber("123");
        user.setPassword("secret123");
        user.setUserCode("USR-999");

        Portfolio portfolio = new Portfolio();
        portfolio.setDebts(new ArrayList<>());
        portfolio.setAmount(0);
        user.setPortfolio(portfolio);

        return user;
    }

    private void saveDefaultUser() {
        userRepository.save(createDefaultUser());
    }
}
