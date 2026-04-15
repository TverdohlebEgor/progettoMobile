package cohappy.backend.controller;

import cohappy.backend.exceptions.IllegalInputException;
import cohappy.backend.exceptions.NoPatchException;
import cohappy.backend.exceptions.NotFoundException;
import cohappy.backend.model.dto.*;
import cohappy.backend.service.HouseAdvertisementService;
import cohappy.backend.service.HouseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cohappy.backend.model.OperationResultMessages.OPERATION_COMPLETED;

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
                return ResponseEntity.ok(OPERATION_COMPLETED);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{houseCode}")
    public ResponseEntity<String> deleteHouse(@PathVariable String houseCode) {
        try {
            if (houseService.deleteHouse(houseCode)) {
                return ResponseEntity.ok(OPERATION_COMPLETED);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PatchMapping("/advertisement/modify")
    public ResponseEntity<String> modifyHouseAdvertisement(@RequestBody ModifyHouseAdvertisementDTO modifyHouseAdvertisementDTO) {
        try {
            houseAdvertisementService.modifyHouseAdvertisement(modifyHouseAdvertisementDTO);
            return ResponseEntity.ok(OPERATION_COMPLETED);
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (NoPatchException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PatchMapping("/modify")
    public ResponseEntity<String> modifyHouse(@RequestBody ModifyHouseDTO modifyHouseDTO) {
        try {
            houseService.modifyHouse(modifyHouseDTO);
            return ResponseEntity.ok(OPERATION_COMPLETED);
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (NoPatchException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/advertisement/create")
    public ResponseEntity<String> createHouseAdvertisement(@RequestBody CreateHouseAdvertisementDTO createHouseAdvertisementDTO) {
        try {
            houseAdvertisementService.createHouseAdvertisement(createHouseAdvertisementDTO);
            return ResponseEntity.ok(OPERATION_COMPLETED);
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/add/admin")
    public ResponseEntity<String> addAdmin(@RequestBody AddAdminDTO addAdminDTO) {
        try {
            houseService.addAdmin(addAdminDTO);
            return ResponseEntity.ok(OPERATION_COMPLETED);
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/remove/admin")
    public ResponseEntity<String> removeAdmin(@RequestBody RemoveAdminDTO removeAdminDTO) {
        try {
            houseService.removeAdmin(removeAdminDTO);
            return ResponseEntity.ok(OPERATION_COMPLETED);
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/add/user")
    public ResponseEntity<String> addUser(@RequestBody AddUserDTO addUserDTO) {
        try {
            houseService.addUser(addUserDTO);
            return ResponseEntity.ok(OPERATION_COMPLETED);
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/remove/user")
    public ResponseEntity<String> removeUser(@RequestBody RemoveUserDTO removeUserDTO) {
        try {
            houseService.removeUser(removeUserDTO);
            return ResponseEntity.ok(OPERATION_COMPLETED);
        } catch (IllegalInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}

