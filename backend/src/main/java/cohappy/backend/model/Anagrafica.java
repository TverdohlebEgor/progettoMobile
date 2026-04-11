package cohappy.backend.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Anagrafica {
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String cv;
    private int age;
}
