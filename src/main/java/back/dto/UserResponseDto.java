package back.dto;

import java.time.LocalDate;
import java.util.Set;
import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private LocalDate birthdate;
    private String phoneNumber;
    private Set<String> rolesName;
}
