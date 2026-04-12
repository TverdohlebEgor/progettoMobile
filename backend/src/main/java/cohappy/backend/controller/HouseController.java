package cohappy.backend.controller;

import cohappy.backend.exceptions.IllegalInputException;
import cohappy.backend.exceptions.NoPatchException;
import cohappy.backend.exceptions.NotFoundException;
import cohappy.backend.model.dto.*;
import cohappy.backend.service.HouseAdvertisementService;
import cohappy.backend.service.HouseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/house")
@Slf4j
@AllArgsConstructor
public class HouseController {
    private final HouseService houseService;
    private final HouseAdvertisementService houseAdvertisementService;

    @GetMapping("/advertisement/all")
    public ResponseEntity<List<GetHouseAdvertesimentDTO>> getAllHouseAdvertisement() {
        try {
            List<GetHouseAdvertesimentDTO> houseAdvertisement = houseAdvertisementService.getAllHouseAdvertisement();
            return ResponseEntity.ok(houseAdvertisement);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/advertisement/{houseCode}")
    public ResponseEntity<GetHouseAdvertesimentDTO> getHouseAdvertisement(@PathVariable String houseCode) {
        try {
            GetHouseAdvertesimentDTO houseAdvertisement = houseAdvertisementService.getHouseAdvertisement(houseCode);
            return ResponseEntity.ok(houseAdvertisement);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{houseCode}")
    public ResponseEntity<GetHouseDTO> getHouse(@PathVariable String houseCode) {
        try {
            GetHouseDTO house = houseService.getHouse(houseCode);
            return ResponseEntity.ok(house);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/advertisement/delete/{houseCode}")
    public ResponseEntity<String> deleteHouseAdvertisement(@PathVariable String houseCode) {
        try {
            if (houseAdvertisementService.deleteHouseAdvertisement(houseCode)) {
                return ResponseEntity.ok("Account deleted correctly");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{houseCode}")
    public ResponseEntity<String> deleteHouse(@PathVariable String houseCode) {
        try {
            if (houseService.deleteHouse(houseCode)) {
                return ResponseEntity.ok("Account deleted correctly");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PatchMapping("/advertisement/modify")
    public ResponseEntity<String> modifyHouseAdvertisement(@RequestBody ModifyHouseAdvertisementDTO modifyHouseAdvertisementDTO) {
        try {
            houseAdvertisementService.modifyHouseAdvertisement(modifyHouseAdvertisementDTO);
            return ResponseEntity.ok("Patched correctly");
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NoPatchException e) {
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/modify")
    public ResponseEntity<String> modifyHouse(@RequestBody ModifyHouseDTO modifyHouseDTO) {
        try {
            houseService.modifyHouse(modifyHouseDTO);
            return ResponseEntity.ok("Patched correctly");
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NoPatchException e) {
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/advertisement/create")
    public ResponseEntity<String> createHouseAdvertisement(@RequestBody CreateHouseAdvertisementDTO createHouseAdvertisementDTO) {
        try {
            houseAdvertisementService.createHouseAdvertisement(createHouseAdvertisementDTO);
            return ResponseEntity.ok("created correctly");
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createHouse(@RequestBody CreateHouseDTO createHouseDTO) {
        try {
            String houseCode = houseService.createHouse(createHouseDTO);
            return ResponseEntity.ok(houseCode);
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/add/admin")
    public ResponseEntity<String> addAdmin(@RequestBody AddAdminDTO addAdminDTO) {
        try {
            houseService.addAdmin(addAdminDTO);
            return ResponseEntity.ok("User %s is now admin of house %s".formatted(addAdminDTO.getUserCode(),addAdminDTO.getHouseCode()));
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/remove/admin")
    public ResponseEntity<String> removeAdmin(@RequestBody RemoveAdminDTO removeAdminDTO) {
        try {
            houseService.removeAdmin(removeAdminDTO);
            return ResponseEntity.ok("User %s is not an admin anymore of house %s".formatted(removeAdminDTO.getUserCode(),removeAdminDTO.getHouseCode()));
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/add/user")
    public ResponseEntity<String> addUser(@RequestBody AddUserDTO addUserDTO) {
        try {
            houseService.addUser(addUserDTO);
            return ResponseEntity.ok("User %s has been added to the house %s".formatted(addUserDTO.getUserCode(),addUserDTO.getHouseCode()));
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/remove/user")
    public ResponseEntity<String> removeUser(@RequestBody RemoveUserDTO removeUserDTO) {
        try {
            houseService.removeUser(removeUserDTO);
            return ResponseEntity.ok("User %s is not an user anymore of house %s".formatted(removeUserDTO.getUserCode(),removeUserDTO.getHouseCode()));
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

