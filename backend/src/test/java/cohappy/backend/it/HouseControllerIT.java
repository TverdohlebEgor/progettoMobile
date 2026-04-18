package cohappy.backend.it;

import cohappy.backend.model.*;
import cohappy.backend.model.dto.request.*;
import cohappy.backend.repositories.HouseAdvertisementRepository;
import cohappy.backend.repositories.HouseRepository;
import cohappy.backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class HouseControllerIT extends BaseIT{
    @Autowired
    private HouseAdvertisementRepository houseAdvertisementRepository;

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        houseRepository.deleteAll();
        userRepository.deleteAll();
        houseAdvertisementRepository.deleteAll();
    }

    private String path(String str) {
        return "/api/house" + str;
    }

    @Test
    void shouldGetAllHouseAdvertisement() throws Exception {
        createDefaultEnv();
        mockMvc.perform(get(path("/advertisement/all"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].houseCode").value("houseCode"))
                .andExpect(jsonPath("$[0].images").isArray())
                .andExpect(jsonPath("$[0].country").value("Italy"))
                .andExpect(jsonPath("$[0].region").value("RA"))
                .andExpect(jsonPath("$[0].street").value("Via dal pozzo"))
                .andExpect(jsonPath("$[0].civicNumber").value(37))
                .andExpect(jsonPath("$[0].state").value("PUBLIC"))
                .andExpect(jsonPath("$[0].publishedByCode").value("USR-999"))
                .andExpect(jsonPath("$[0].publishedByImages").isArray())
                .andExpect(jsonPath("$[0].publishedByEmail").value("test@cohappy.it"))
                .andExpect(jsonPath("$[0].publishedByPhoneNumber").value("123"))
                .andExpect(jsonPath("$[0].description").value("desc"));
    }

    @Test
    void shouldGetHouseAdvertisement() throws Exception {
        createDefaultEnv();
        mockMvc.perform(get(path("/advertisement/houseCode"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.houseCode").value("houseCode"))
                .andExpect(jsonPath("$.images").isArray())
                .andExpect(jsonPath("$.country").value("Italy"))
                .andExpect(jsonPath("$.region").value("RA"))
                .andExpect(jsonPath("$.street").value("Via dal pozzo"))
                .andExpect(jsonPath("$.civicNumber").value(37))
                .andExpect(jsonPath("$.state").value("PUBLIC"))
                .andExpect(jsonPath("$.publishedByCode").value("USR-999"))
                .andExpect(jsonPath("$.publishedByImages").isArray())
                .andExpect(jsonPath("$.publishedByEmail").value("test@cohappy.it"))
                .andExpect(jsonPath("$.publishedByPhoneNumber").value("123"))
                .andExpect(jsonPath("$.description").value("desc"));
    }

    @Test
    void shouldNotFoundGetHouseAdvertisement() throws Exception {
        saveDefaultHouseAdvertismente();
        mockMvc.perform(get(path("/advertisement/NOTEXISTING"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetHouse() throws Exception {
        saveDefaultHouse();
        mockMvc.perform(get(path("/houseCode"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.houseCode").value("houseCode"))
                .andExpect(jsonPath("$.admins").isArray())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.images").isArray())
                .andExpect(jsonPath("$.costPerMonth").value(100))
                .andExpect(jsonPath("$.country").value("Italy"))
                .andExpect(jsonPath("$.region").value("RA"))
                .andExpect(jsonPath("$.street").value("Via dal pozzo"))
                .andExpect(jsonPath("$.civicNumber").value(37));
    }

    @Test
    void shouldNotFoundGetHouse() throws Exception {
        saveDefaultHouseAdvertismente();
        mockMvc.perform(get(path("/NOTEXISTING"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteHouseAdvertisement() throws Exception {
        saveDefaultHouseAdvertismente();
        Optional<HouseAdvertisement> houseAdvertisement = houseAdvertisementRepository.findByHouseCode("houseCode");
        assertThat(houseAdvertisement).isPresent();
        mockMvc.perform(delete(path("/advertisement/delete/houseCode"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        houseAdvertisement = houseAdvertisementRepository.findByHouseCode("houseCode");
        assertThat(houseAdvertisement).isNotPresent();
    }

    @Test
    void shouldNotFoundDeleteHouseAdvertisement() throws Exception {
        saveDefaultHouseAdvertismente();
        Optional<HouseAdvertisement> houseAdvertisement = houseAdvertisementRepository.findByHouseCode("houseCode");
        assertThat(houseAdvertisement).isPresent();
        mockMvc.perform(delete(path("/advertisement/delete/NOTEXISTING"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        houseAdvertisement = houseAdvertisementRepository.findByHouseCode("houseCode");
        assertThat(houseAdvertisement).isPresent();
    }

    @Test
    void shouldDeleteHouse() throws Exception {
        saveDefaultHouse();
        Optional<House> house = houseRepository.findByHouseCode("houseCode");
        assertThat(house).isPresent();
        mockMvc.perform(delete(path("/delete/houseCode"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        house = houseRepository.findByHouseCode("houseCode");
        assertThat(house).isNotPresent();
    }

    @Test
    void shouldNotFoundDeleteHouse() throws Exception {
        saveDefaultHouse();
        Optional<House> house = houseRepository.findByHouseCode("houseCode");
        assertThat(house).isPresent();
        mockMvc.perform(delete(path("/delete/NOTEXISTING"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        house = houseRepository.findByHouseCode("houseCode");
        assertThat(house).isPresent();
    }

    @Test
    void shouldPatchHouseAdvertisement() throws Exception {
        saveDefaultHouseAdvertismente();

        ModifyHouseAdvertisementDTO request = new ModifyHouseAdvertisementDTO();
        request.setHouseCode("houseCode");
        request.setState(HouseState.PRIVATE);
        request.setDescription("desc2");

        HouseAdvertisement houseAdvertisement = houseAdvertisementRepository.findByHouseCode("houseCode")
                .orElseThrow(() -> new AssertionError("House advertisement not found"));
        assertThat(houseAdvertisement.getState()).isEqualTo(HouseState.PUBLIC);

        mockMvc.perform(patch(path("/advertisement/modify"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        houseAdvertisement = houseAdvertisementRepository.findByHouseCode("houseCode")
                .orElseThrow(() -> new AssertionError("House advertisement not found"));
        assertThat(houseAdvertisement.getState()).isEqualTo(HouseState.PRIVATE);
        assertThat(houseAdvertisement.getDescription()).isEqualTo("desc2");
    }

    @Test
    void shouldNotFoundPatchHouseAdvertisement() throws Exception {
        saveDefaultHouseAdvertismente();

        ModifyHouseAdvertisementDTO request = new ModifyHouseAdvertisementDTO();
        request.setHouseCode("NOTEXSTING");
        request.setState(HouseState.PRIVATE);

        mockMvc.perform(patch(path("/advertisement/modify"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldBadRequestPatchHouseAdvertisement() throws Exception {
        saveDefaultHouseAdvertismente();

        ModifyHouseAdvertisementDTO request = new ModifyHouseAdvertisementDTO();
        request.setHouseCode("houseCode");

        mockMvc.perform(patch(path("/advertisement/modify"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestNoHouseCodePatchHouseAdvertisement() throws Exception {
        saveDefaultHouseAdvertismente();

        ModifyHouseAdvertisementDTO request = new ModifyHouseAdvertisementDTO();
        request.setHouseCode(null);

        mockMvc.perform(patch(path("/advertisement/modify"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNoPatchHouseCodePatchHouseAdvertisement() throws Exception {
        saveDefaultHouseAdvertismente();

        ModifyHouseAdvertisementDTO request = new ModifyHouseAdvertisementDTO();
        request.setHouseCode("houseCode");
        request.setState(HouseState.PUBLIC);

        mockMvc.perform(patch(path("/advertisement/modify"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldPatchHouse() throws Exception {
        saveDefaultHouse();

        ModifyHouseDTO request = new ModifyHouseDTO();
        request.setHouseCode("houseCode");
        request.setRegion("BO");
        request.setCivicNumber(40);
        request.setStreet("street");
        request.setCountry("country");
        request.setImages(new ArrayList<>());
        request.setCostPerMonth(123);

        House house = houseRepository.findByHouseCode("houseCode")
                .orElseThrow(() -> new AssertionError("House not found"));
        assertThat(house.getCostPerMonth()).isEqualTo(100);

        mockMvc.perform(patch(path("/modify"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        house = houseRepository.findByHouseCode("houseCode")
                .orElseThrow(() -> new AssertionError("House advertisement not found"));
        assertThat(house.getLocation().getRegion()).isEqualTo("BO");
        assertThat(house.getLocation().getCivicNumber()).isEqualTo(40);
        assertThat(house.getLocation().getStreet()).isEqualTo("street");
        assertThat(house.getLocation().getCountry()).isEqualTo("country");
        assertThat(house.getImages().size()).isZero();
        assertThat(house.getCostPerMonth()).isEqualTo(123);
    }

    @Test
    void shouldNotFoundPatchHouse() throws Exception {
        saveDefaultHouse();

        ModifyHouseDTO request = new ModifyHouseDTO();
        request.setHouseCode("NOTEXSTING");
        request.setCountry("Country");

        mockMvc.perform(patch(path("/modify"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldBadRequestPatchHouse() throws Exception {
        saveDefaultHouse();

        ModifyHouseDTO request = new ModifyHouseDTO();
        request.setHouseCode("houseCode");

        mockMvc.perform(patch(path("/modify"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestNoCodePatchHouse() throws Exception {
        saveDefaultHouse();

        ModifyHouseDTO request = new ModifyHouseDTO();
        request.setHouseCode(null);

        mockMvc.perform(patch(path("/modify"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNoPatchHouse() throws Exception {
        saveDefaultHouse();

        ModifyHouseDTO request = new ModifyHouseDTO();
        request.setHouseCode("houseCode");
        request.setCostPerMonth(100);

        mockMvc.perform(patch(path("/modify"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldCreateHouseAdvertisement() throws Exception {
        saveDefaultHouse();
        saveDefaultUser();

        CreateHouseAdvertisementDTO request = new CreateHouseAdvertisementDTO();
        request.setHouseCode("houseCode");
        request.setPublishedBy("USR-999");
        request.setState(HouseState.PUBLIC);

        mockMvc.perform(post(path("/advertisement/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        HouseAdvertisement houseAdvertisement = houseAdvertisementRepository.findByHouseCode(request.getHouseCode())
                .orElseThrow(() -> new AssertionError("House advertismente not created"));
        assertThat(houseAdvertisement.getState()).isEqualTo(HouseState.PUBLIC);
        assertThat(houseAdvertisement.getPublishedBy()).isEqualTo("USR-999");
    }

    @Test
    void shouldFailCreateForCodeHouseAdvertisement() throws Exception {
        saveDefaultHouse();
        saveDefaultUser();

        CreateHouseAdvertisementDTO request = new CreateHouseAdvertisementDTO();
        request.setHouseCode(null);
        request.setPublishedBy("USR-999");
        request.setState(HouseState.PUBLIC);

        mockMvc.perform(post(path("/advertisement/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotFoundHouseCreateHouseAdvertisement() throws Exception {
        //saveDefaultHouse();
        saveDefaultUser();

        CreateHouseAdvertisementDTO request = new CreateHouseAdvertisementDTO();
        request.setHouseCode("houseCode");
        request.setPublishedBy("USR-999");
        request.setState(HouseState.PUBLIC);

        mockMvc.perform(post(path("/advertisement/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAlreadyPresentHouseCreateHouseAdvertisement() throws Exception {
        saveDefaultHouseAdvertismente();
        saveDefaultHouse();
        saveDefaultUser();

        CreateHouseAdvertisementDTO request = new CreateHouseAdvertisementDTO();
        request.setHouseCode("houseCode");
        request.setPublishedBy("USR-999");
        request.setState(HouseState.PUBLIC);

        mockMvc.perform(post(path("/advertisement/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailNoPublishedByCreateHouseAdvertisement() throws Exception {
        saveDefaultHouse();
        saveDefaultUser();

        CreateHouseAdvertisementDTO request = new CreateHouseAdvertisementDTO();
        request.setHouseCode("houseCode");
        request.setPublishedBy(null);
        request.setState(HouseState.PUBLIC);

        mockMvc.perform(post(path("/advertisement/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailNoUserCreateHouseAdvertisement() throws Exception {
        saveDefaultHouse();

        CreateHouseAdvertisementDTO request = new CreateHouseAdvertisementDTO();
        request.setHouseCode("houseCode");
        request.setPublishedBy("USR-999");
        request.setState(HouseState.PUBLIC);

        mockMvc.perform(post(path("/advertisement/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateHouse() throws Exception {
        saveDefaultUser();

        CreateHouseDTO request = new CreateHouseDTO();
        request.setStreet("street");
        request.setRegion("RA");
        request.setCountry("Italy");
        request.setCivicNumber(37);
        request.setImages(new ArrayList<>());
        request.setUserCode("USR-999");
        request.setCostPerMonth(100);

        AtomicReference<String> houseCode = new AtomicReference<>();
        mockMvc.perform(post(path("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(result -> houseCode.set(result.getResponse().getContentAsString()));

        House house = houseRepository.findByHouseCode(houseCode.get())
                .orElseThrow(() -> new AssertionError("House not created"));

        assertThat(house.getLocation().getStreet()).isEqualTo("street");
        assertThat(house.getLocation().getRegion()).isEqualTo("RA");
        assertThat(house.getLocation().getCountry()).isEqualTo("Italy");
        assertThat(house.getLocation().getCivicNumber()).isEqualTo(37);
        assertThat(house.getImages().size()).isZero();
        assertThat(house.getAdmins().getFirst()).isEqualTo("USR-999");
        assertThat(house.getUsers().size()).isZero();
    }

    @Test
    void shouldBadRequestForUserCodeCreateHouse() throws Exception {
        saveDefaultUser();

        CreateHouseDTO request = new CreateHouseDTO();
        request.setStreet("street");
        request.setRegion("RA");
        request.setCountry("Italy");
        request.setCivicNumber(37);
        request.setImages(new ArrayList<>());
        request.setUserCode("NOTEXISTING");
        request.setCostPerMonth(100);

        mockMvc.perform(post(path("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldBadRequestForUserCode2CreateHouse() throws Exception {
        saveDefaultUser();

        CreateHouseDTO request = new CreateHouseDTO();
        request.setStreet("street");
        request.setRegion("RA");
        request.setCountry("Italy");
        request.setCivicNumber(37);
        request.setImages(new ArrayList<>());
        request.setUserCode(null);
        request.setCostPerMonth(100);

        mockMvc.perform(post(path("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestForStreetCreateHouse() throws Exception {
        saveDefaultUser();

        CreateHouseDTO request = new CreateHouseDTO();
        request.setStreet("  ");
        request.setRegion("RA");
        request.setCountry("Italy");
        request.setCivicNumber(37);
        request.setImages(new ArrayList<>());
        request.setUserCode("USR-999");
        request.setCostPerMonth(100);

        mockMvc.perform(post(path("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestForRegionCreateHouse() throws Exception {
        saveDefaultUser();

        CreateHouseDTO request = new CreateHouseDTO();
        request.setStreet("street");
        request.setRegion("  ");
        request.setCountry("Italy");
        request.setCivicNumber(37);
        request.setImages(new ArrayList<>());
        request.setUserCode("USR-999");
        request.setCostPerMonth(100);

        mockMvc.perform(post(path("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestForCountryCreateHouse() throws Exception {
        saveDefaultUser();

        CreateHouseDTO request = new CreateHouseDTO();
        request.setStreet("street");
        request.setRegion("RA");
        request.setCountry(" ");
        request.setCivicNumber(37);
        request.setImages(new ArrayList<>());
        request.setUserCode("USR-999");
        request.setCostPerMonth(100);

        mockMvc.perform(post(path("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestForCivicNumberCreateHouse() throws Exception {
        saveDefaultUser();

        CreateHouseDTO request = new CreateHouseDTO();
        request.setStreet("street");
        request.setRegion("RA");
        request.setCountry("Italy");
        request.setCivicNumber(null);
        request.setImages(new ArrayList<>());
        request.setUserCode("USR-999");
        request.setCostPerMonth(100);

        mockMvc.perform(post(path("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestForCostPerMonthCreateHouse() throws Exception {
        saveDefaultUser();

        CreateHouseDTO request = new CreateHouseDTO();
        request.setStreet("street");
        request.setRegion("RA");
        request.setCountry("Italy");
        request.setCivicNumber(10);
        request.setImages(new ArrayList<>());
        request.setUserCode("USR-999");
        request.setCostPerMonth(null);

        mockMvc.perform(post(path("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAddAdminHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user1.getUserCode());
        house.getUsers().add(user2.getUserCode());

        userRepository.save(user1);
        userRepository.save(user2);
        houseRepository.save(house);

        AddAdminDTO request = new AddAdminDTO();
        request.setUserCode(user2.getUserCode());
        request.setHouseCode(house.getHouseCode());


        mockMvc.perform(post(path("/add/admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        house = houseRepository.findByHouseCode(house.getHouseCode())
                .orElseThrow(() -> new AssertionError("House not founded"));

        assertThat(house.getAdmins().size()).isEqualTo(2);
        assertThat(house.getAdmins().contains("USR-2")).isTrue();
    }

    @Test
    void shouldFailAddAlreadyAdminHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user1.getUserCode());
        house.getAdmins().add(user2.getUserCode());

        userRepository.save(user1);
        userRepository.save(user2);
        houseRepository.save(house);

        AddAdminDTO request = new AddAdminDTO();
        request.setUserCode(user2.getUserCode());
        request.setHouseCode(house.getHouseCode());


        mockMvc.perform(post(path("/add/admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailNoUserCodeAddAdminHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user1.getUserCode());
        house.getUsers().add(user2.getUserCode());

        userRepository.save(user1);
        userRepository.save(user2);
        houseRepository.save(house);

        AddAdminDTO request = new AddAdminDTO();
        request.setUserCode(null);
        request.setHouseCode(house.getHouseCode());


        mockMvc.perform(post(path("/add/admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailNoHouseCodeAddAdminHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user1.getUserCode());
        house.getUsers().add(user2.getUserCode());

        userRepository.save(user1);
        userRepository.save(user2);
        houseRepository.save(house);

        AddAdminDTO request = new AddAdminDTO();
        request.setUserCode(user2.getUserCode());
        request.setHouseCode(null);


        mockMvc.perform(post(path("/add/admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailNoHouseAddAdminHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user1.getUserCode());
        house.getUsers().add(user2.getUserCode());

        userRepository.save(user1);
        userRepository.save(user2);

        AddAdminDTO request = new AddAdminDTO();
        request.setUserCode(user2.getUserCode());
        request.setHouseCode(house.getHouseCode());


        mockMvc.perform(post(path("/add/admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailNoUserAddAdminHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user1.getUserCode());
        house.getUsers().add(user2.getUserCode());

        userRepository.save(user1);
        houseRepository.save(house);

        AddAdminDTO request = new AddAdminDTO();
        request.setUserCode(user2.getUserCode());
        request.setHouseCode(house.getHouseCode());


        mockMvc.perform(post(path("/add/admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRemoveAdminHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user1.getUserCode());
        house.getAdmins().add(user2.getUserCode());

        userRepository.save(user1);
        userRepository.save(user2);
        houseRepository.save(house);

        RemoveAdminDTO request = new RemoveAdminDTO();
        request.setUserCode(user2.getUserCode());
        request.setHouseCode(house.getHouseCode());


        mockMvc.perform(post(path("/remove/admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        house = houseRepository.findByHouseCode(house.getHouseCode())
                .orElseThrow(() -> new AssertionError("House not founded"));

        assertThat(house.getAdmins().size()).isEqualTo(1);
        assertThat(house.getAdmins().contains("USR-2")).isFalse();
    }

    @Test
    void shouldFailNoUserCodeRemoveAdminHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user1.getUserCode());
        house.getAdmins().add(user2.getUserCode());

        userRepository.save(user1);
        userRepository.save(user2);
        houseRepository.save(house);

        RemoveAdminDTO request = new RemoveAdminDTO();
        request.setUserCode(null);
        request.setHouseCode(house.getHouseCode());


        mockMvc.perform(post(path("/remove/admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailNoHouseCodeRemoveAdminHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user1.getUserCode());
        house.getAdmins().add(user2.getUserCode());

        userRepository.save(user1);
        userRepository.save(user2);
        houseRepository.save(house);

        RemoveAdminDTO request = new RemoveAdminDTO();
        request.setUserCode(user2.getUserCode());
        request.setHouseCode(null);


        mockMvc.perform(post(path("/remove/admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailIsNoAdminCodeRemoveAdminHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user1.getUserCode());

        userRepository.save(user1);
        userRepository.save(user2);
        houseRepository.save(house);

        RemoveAdminDTO request = new RemoveAdminDTO();
        request.setUserCode(user2.getUserCode());
        request.setHouseCode(house.getHouseCode());


        mockMvc.perform(post(path("/remove/admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailIsLastAdminCodeRemoveAdminHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user2.getUserCode());

        userRepository.save(user1);
        userRepository.save(user2);
        houseRepository.save(house);

        RemoveAdminDTO request = new RemoveAdminDTO();
        request.setUserCode(user2.getUserCode());
        request.setHouseCode(house.getHouseCode());


        mockMvc.perform(post(path("/remove/admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailNotFoundUserCodeRemoveAdminHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user2.getUserCode());

        userRepository.save(user1);
        houseRepository.save(house);

        RemoveAdminDTO request = new RemoveAdminDTO();
        request.setUserCode(user2.getUserCode());
        request.setHouseCode(house.getHouseCode());


        mockMvc.perform(post(path("/remove/admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailNotFoundHouseCodeRemoveAdminHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user2.getUserCode());

        userRepository.save(user1);
        userRepository.save(user2);

        RemoveAdminDTO request = new RemoveAdminDTO();
        request.setUserCode(user2.getUserCode());
        request.setHouseCode(house.getHouseCode());


        mockMvc.perform(post(path("/remove/admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAddUserHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user1.getUserCode());

        userRepository.save(user1);
        userRepository.save(user2);
        houseRepository.save(house);

        AddUserDTO request = new AddUserDTO();
        request.setUserCode(user2.getUserCode());
        request.setHouseCode(house.getHouseCode());


        mockMvc.perform(post(path("/add/user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        house = houseRepository.findByHouseCode(house.getHouseCode())
                .orElseThrow(() -> new AssertionError("House not founded"));

        assertThat(house.getAdmins().size()).isEqualTo(1);
        assertThat(house.getUsers().contains("USR-2")).isTrue();
    }

    @Test
    void shouldFailAddAlreadyUserHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user1.getUserCode());
        house.getUsers().add(user2.getUserCode());

        userRepository.save(user1);
        userRepository.save(user2);
        houseRepository.save(house);

        AddUserDTO request = new AddUserDTO();
        request.setUserCode(user2.getUserCode());
        request.setHouseCode(house.getHouseCode());


        mockMvc.perform(post(path("/add/user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailNoUserCodeAddUserHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user1.getUserCode());

        userRepository.save(user1);
        userRepository.save(user2);
        houseRepository.save(house);

        AddUserDTO request = new AddUserDTO();
        request.setUserCode(null);
        request.setHouseCode(house.getHouseCode());


        mockMvc.perform(post(path("/add/user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailNoHouseCodeAddUserHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user1.getUserCode());
        house.getUsers().add(user2.getUserCode());

        userRepository.save(user1);
        userRepository.save(user2);
        houseRepository.save(house);

        AddUserDTO request = new AddUserDTO();
        request.setUserCode(user2.getUserCode());
        request.setHouseCode(null);


        mockMvc.perform(post(path("/add/user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailNoHouseAddUserHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user1.getUserCode());

        userRepository.save(user1);
        userRepository.save(user2);

        AddUserDTO request = new AddUserDTO();
        request.setUserCode(user2.getUserCode());
        request.setHouseCode(house.getHouseCode());


        mockMvc.perform(post(path("/add/user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailNoUserAddUserHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user1.getUserCode());

        userRepository.save(user1);
        houseRepository.save(house);

        AddUserDTO request = new AddUserDTO();
        request.setUserCode(user2.getUserCode());
        request.setHouseCode(house.getHouseCode());


        mockMvc.perform(post(path("/add/user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRemoveUserHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getUsers().add(user1.getUserCode());
        house.getUsers().add(user2.getUserCode());

        userRepository.save(user1);
        userRepository.save(user2);
        houseRepository.save(house);

        RemoveUserDTO request = new RemoveUserDTO();
        request.setUserCode(user2.getUserCode());
        request.setHouseCode(house.getHouseCode());


        mockMvc.perform(post(path("/remove/user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        house = houseRepository.findByHouseCode(house.getHouseCode())
                .orElseThrow(() -> new AssertionError("House not founded"));

        assertThat(house.getUsers().size()).isEqualTo(1);
        assertThat(house.getUsers().contains("USR-2")).isFalse();
    }

    @Test
    void shouldFailNoUserCodeRemoveUserHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user1.getUserCode());
        house.getUsers().add(user2.getUserCode());

        userRepository.save(user1);
        userRepository.save(user2);
        houseRepository.save(house);

        RemoveUserDTO request = new RemoveUserDTO();
        request.setUserCode(null);
        request.setHouseCode(house.getHouseCode());


        mockMvc.perform(post(path("/remove/user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailNoHouseCodeRemoveUserHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user1.getUserCode());
        house.getUsers().add(user2.getUserCode());

        userRepository.save(user1);
        userRepository.save(user2);
        houseRepository.save(house);

        RemoveUserDTO request = new RemoveUserDTO();
        request.setUserCode(user2.getUserCode());
        request.setHouseCode(null);


        mockMvc.perform(post(path("/remove/user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailIsNoUserCodeRemoveUserHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getUsers().add(user1.getUserCode());

        userRepository.save(user1);
        userRepository.save(user2);
        houseRepository.save(house);

        RemoveUserDTO request = new RemoveUserDTO();
        request.setUserCode(user2.getUserCode());
        request.setHouseCode(house.getHouseCode());


        mockMvc.perform(post(path("/remove/user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailNotFoundUserCodeRemoveUserHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user2.getUserCode());

        userRepository.save(user1);
        houseRepository.save(house);

        RemoveUserDTO request = new RemoveUserDTO();
        request.setUserCode(user2.getUserCode());
        request.setHouseCode(house.getHouseCode());


        mockMvc.perform(post(path("/remove/user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailNotFoundHouseCodeRemoveUserHouse() throws Exception {
        UserAccount user1 = createDefaultUser();
        user1.setUserCode("USR-1");
        UserAccount user2 = createDefaultUser();
        user2.setUserCode("USR-2");

        House house = createDefaultHouse();
        house.getAdmins().add(user2.getUserCode());

        userRepository.save(user1);
        userRepository.save(user2);

        RemoveUserDTO request = new RemoveUserDTO();
        request.setUserCode(user2.getUserCode());
        request.setHouseCode(house.getHouseCode());


        mockMvc.perform(post(path("/remove/user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
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

    private HouseAdvertisement createDefaultHouseAdvertisement() {
        HouseAdvertisement houseAdvertisement = new HouseAdvertisement();
        houseAdvertisement.setHouseCode("houseCode");
        houseAdvertisement.setState(HouseState.PUBLIC);
        houseAdvertisement.setPublishedBy("USR-999");
        houseAdvertisement.setDescription("desc");
        return houseAdvertisement;
    }

    private void saveDefaultHouseAdvertismente() {
        houseAdvertisementRepository.save(createDefaultHouseAdvertisement());
    }

    private void createDefaultEnv() {
        saveDefaultUser();
        saveDefaultHouse();
        saveDefaultHouseAdvertismente();
    }
}
