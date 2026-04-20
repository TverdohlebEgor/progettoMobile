package cohappy.backend.it;

import cohappy.backend.model.House;
import cohappy.backend.model.HouseChore;
import cohappy.backend.model.Location;
import cohappy.backend.model.Portfolio;
import cohappy.backend.model.UserAccount;
import cohappy.backend.model.dto.request.CreateChoreDTO;
import cohappy.backend.model.dto.request.PatchChoreDTO;
import cohappy.backend.repositories.ChoreRepository;
import cohappy.backend.repositories.HouseRepository;
import cohappy.backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ChoreControllerIT extends BaseIT {

    @Autowired
    private ChoreRepository choreRepository;

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final LocalDate DEFAULT_DATE = LocalDate.of(2025, 1, 15);

    @BeforeEach
    void setUp() {
        choreRepository.deleteAll();
        houseRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String path(String str) {
        return "/api/chore" + str;
    }

    // -------------------------------------------------------------------------
    // GET /api/chore/{houseCode}/{date}
    // -------------------------------------------------------------------------

    @Test
    void shouldGetChoresByHouseCodeAndDate() throws Exception {
        saveDefaultUser();
        saveDefaultHouse();
        saveDefaultChore();

        mockMvc.perform(get(path("/houseCode/" + DEFAULT_DATE))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].choreCode").value("choreCode"))
                .andExpect(jsonPath("$[0].name").value("Clean kitchen"))
                .andExpect(jsonPath("$[0].description").value("desc"))
                .andExpect(jsonPath("$[0].houseCode").value("houseCode"))
                .andExpect(jsonPath("$[0].assignedTo").exists())
                .andExpect(jsonPath("$[0].completed").exists());
    }

    @Test
    void shouldGetEmptyListWhenNoChoresOnDate() throws Exception {
        saveDefaultUser();
        saveDefaultHouse();
        saveDefaultChore();

        LocalDate otherDate = DEFAULT_DATE.plusDays(1);

        mockMvc.perform(get(path("/houseCode/" + otherDate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnNotFoundWhenUserAssignedToChoreIsMissing() throws Exception {
        // House exists but the user referenced inside the chore does not
        saveDefaultHouse();
        saveDefaultChore(); // chore references USR-999, but user is not saved

        mockMvc.perform(get(path("/houseCode/" + DEFAULT_DATE))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // -------------------------------------------------------------------------
    // POST /api/chore/create
    // -------------------------------------------------------------------------

    @Test
    void shouldCreateChore() throws Exception {
        saveDefaultUser();
        saveDefaultHouse();

        CreateChoreDTO request = buildDefaultCreateChoreDTO();

        mockMvc.perform(post(path("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        List<HouseChore> chores = choreRepository.findByHouseCode("houseCode");
        assertThat(chores.isEmpty()).isFalse();
        assertThat(chores.get(0).getName()).isEqualTo("Clean kitchen");
        assertThat(chores.get(0).getDays().contains(DEFAULT_DATE)).isTrue();
        assertThat(chores.get(0).getAssignedTo().get(DEFAULT_DATE)).isEqualTo("USR-999");
    }

    @Test
    void shouldNotFoundHouseCreateChore() throws Exception {
        saveDefaultUser();
        // house is NOT saved

        CreateChoreDTO request = buildDefaultCreateChoreDTO();

        mockMvc.perform(post(path("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotFoundUserCreateChore() throws Exception {
        saveDefaultHouse();
        // user is NOT saved

        CreateChoreDTO request = buildDefaultCreateChoreDTO();

        mockMvc.perform(post(path("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldBadRequestEmptyDaysCreateChore() throws Exception {
        saveDefaultUser();
        saveDefaultHouse();

        CreateChoreDTO request = buildDefaultCreateChoreDTO();
        request.setDays(new ArrayList<>());
        request.setAssignedTo(new HashMap<>());

        mockMvc.perform(post(path("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestMissingAssignedUserForDayCreateChore() throws Exception {
        saveDefaultUser();
        saveDefaultHouse();

        CreateChoreDTO request = buildDefaultCreateChoreDTO();
        // day is listed but no matching entry in assignedTo map
        request.setAssignedTo(new HashMap<>());

        mockMvc.perform(post(path("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestNullHouseCodeCreateChore() throws Exception {
        saveDefaultUser();
        saveDefaultHouse();

        CreateChoreDTO request = buildDefaultCreateChoreDTO();
        request.setHouseCode(null);

        mockMvc.perform(post(path("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // -------------------------------------------------------------------------
    // PATCH /api/chore/patch
    // -------------------------------------------------------------------------

    @Test
    void shouldPatchChoreAssignedTo() throws Exception {
        saveDefaultUser();

        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-888");
        userRepository.save(user2);

        saveDefaultHouse();
        saveDefaultChore();

        PatchChoreDTO request = new PatchChoreDTO();
        request.setChoreCode("choreCode");
        request.setDay(DEFAULT_DATE);
        request.setAssignedTo("USR-888");

        HouseChore before = choreRepository.findByChoreCode("choreCode")
                .orElseThrow(() -> new AssertionError("Chore not found"));
        assertThat(before.getAssignedTo().get(DEFAULT_DATE)).isEqualTo("USR-999");

        mockMvc.perform(patch(path("/patch"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        HouseChore after = choreRepository.findByChoreCode("choreCode")
                .orElseThrow(() -> new AssertionError("Chore not found"));
        assertThat(after.getAssignedTo().get(DEFAULT_DATE)).isEqualTo("USR-888");
    }

    @Test
    void shouldPatchChoreCompleted() throws Exception {
        saveDefaultUser();
        saveDefaultHouse();
        saveDefaultChore();

        PatchChoreDTO request = new PatchChoreDTO();
        request.setChoreCode("choreCode");
        request.setDay(DEFAULT_DATE);
        request.setCompleted(true);

        HouseChore before = choreRepository.findByChoreCode("choreCode")
                .orElseThrow(() -> new AssertionError("Chore not found"));
        assertThat(before.getCompleted().get(DEFAULT_DATE)).isEqualTo(false);

        mockMvc.perform(patch(path("/patch"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        HouseChore after = choreRepository.findByChoreCode("choreCode")
                .orElseThrow(() -> new AssertionError("Chore not found"));
        assertThat(after.getCompleted().get(DEFAULT_DATE)).isEqualTo(true);
    }

    @Test
    void shouldPatchChoreName() throws Exception {
        saveDefaultUser();
        saveDefaultHouse();
        saveDefaultChore();

        PatchChoreDTO request = new PatchChoreDTO();
        request.setChoreCode("choreCode");
        request.setDay(DEFAULT_DATE);
        request.setName("New name");

        mockMvc.perform(patch(path("/patch"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        HouseChore after = choreRepository.findByChoreCode("choreCode")
                .orElseThrow(() -> new AssertionError("Chore not found"));
        assertThat(after.getName()).isEqualTo("New name");
    }

    @Test
    void shouldPatchChoreDescription() throws Exception {
        saveDefaultUser();
        saveDefaultHouse();
        saveDefaultChore();

        PatchChoreDTO request = new PatchChoreDTO();
        request.setChoreCode("choreCode");
        request.setDay(DEFAULT_DATE);
        request.setDescription("New description");

        mockMvc.perform(patch(path("/patch"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        HouseChore after = choreRepository.findByChoreCode("choreCode")
                .orElseThrow(() -> new AssertionError("Chore not found"));
        assertThat(after.getDescription()).isEqualTo("New description");
    }

    @Test
    void shouldNoContentWhenNoPatchChore() throws Exception {
        saveDefaultUser();
        saveDefaultHouse();
        saveDefaultChore();

        // Same values as the default chore → no actual change
        PatchChoreDTO request = new PatchChoreDTO();
        request.setChoreCode("choreCode");
        request.setDay(DEFAULT_DATE);
        request.setAssignedTo("USR-999");   // same as existing
        request.setCompleted(false);         // same as existing
        request.setName("Clean kitchen");    // same as existing
        request.setDescription("desc");      // same as existing

        mockMvc.perform(patch(path("/patch"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotFoundPatchChore() throws Exception {
        saveDefaultUser();
        saveDefaultHouse();
        saveDefaultChore();

        PatchChoreDTO request = new PatchChoreDTO();
        request.setChoreCode("NOTEXISTING");
        request.setDay(DEFAULT_DATE);
        request.setName("New name");

        mockMvc.perform(patch(path("/patch"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldBadRequestPatchChoreOnDayNotInChore() throws Exception {
        saveDefaultUser();
        saveDefaultHouse();
        saveDefaultChore();

        PatchChoreDTO request = new PatchChoreDTO();
        request.setChoreCode("choreCode");
        request.setDay(DEFAULT_DATE.plusDays(99)); // day not in chore
        request.setName("New name");

        mockMvc.perform(patch(path("/patch"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestNullChoreCodePatchChore() throws Exception {
        saveDefaultUser();
        saveDefaultHouse();
        saveDefaultChore();

        PatchChoreDTO request = new PatchChoreDTO();
        request.setChoreCode(null);
        request.setDay(DEFAULT_DATE);
        request.setName("New name");

        mockMvc.perform(patch(path("/patch"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestNullDayPatchChore() throws Exception {
        saveDefaultUser();
        saveDefaultHouse();
        saveDefaultChore();

        PatchChoreDTO request = new PatchChoreDTO();
        request.setChoreCode("choreCode");
        request.setDay(null);
        request.setName("New name");

        mockMvc.perform(patch(path("/patch"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private HouseChore createDefaultChore() {
        HouseChore chore = new HouseChore();
        chore.setChoreCode("choreCode");
        chore.setHouseCode("houseCode");
        chore.setName("Clean kitchen");
        chore.setDescription("desc");

        Map<LocalDate, String> assignedTo = new HashMap<>();
        assignedTo.put(DEFAULT_DATE, "USR-999");
        chore.setAssignedTo(assignedTo);

        Map<LocalDate, Boolean> completed = new HashMap<>();
        completed.put(DEFAULT_DATE, false);
        chore.setCompleted(completed);

        List<LocalDate> days = new ArrayList<>();
        days.add(DEFAULT_DATE);
        chore.setDays(days);

        return chore;
    }

    private void saveDefaultChore() {
        choreRepository.save(createDefaultChore());
    }

    private UserAccount createDefaultUser() {
        UserAccount user = new UserAccount();
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

    private House createDefaultHouse() {
        House house = new House();
        house.setHouseCode("houseCode");
        house.setAdmins(new ArrayList<>());
        house.setUsers(new ArrayList<>());
        house.setImages(new ArrayList<>());
        house.setCostPerMonth(100);
        house.setChores(new ArrayList<>());

        Location location = new Location();
        location.setCountry("Italy");
        location.setRegion("RA");
        location.setStreet("Via dal pozzo");
        location.setCivicNumber(37);
        house.setLocation(location);

        return house;
    }

    private void saveDefaultHouse() {
        houseRepository.save(createDefaultHouse());
    }

    private CreateChoreDTO buildDefaultCreateChoreDTO() {
        CreateChoreDTO dto = new CreateChoreDTO();
        dto.setHouseCode("houseCode");
        dto.setName("Clean kitchen");
        dto.setDescription("desc");

        List<LocalDate> days = new ArrayList<>();
        days.add(DEFAULT_DATE);
        dto.setDays(days);

        Map<LocalDate, String> assignedTo = new HashMap<>();
        assignedTo.put(DEFAULT_DATE, "USR-999");
        dto.setAssignedTo(assignedTo);

        return dto;
    }
}
