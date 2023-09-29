package back.dto;

import back.lib.ValidAge;
import back.lib.ValidEmail;
import back.lib.ValidPhoneNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

@Data
public class UserRequestDto {
    @NotBlank(message = "email can't be empty or null")
    @ValidEmail
    private String email;
    @NotBlank
    @Size(min = 8, message = "password can't be less than 8")
    private String password;
    @NotBlank(message = "first name can't be empty or null")
    private String firstName;
    @NotBlank(message = "last name can't be empty or null")
    private String lastName;
    @NotBlank(message = "address can't be empty or null")
    private String address;
    @NotBlank(message = "phone number can't be empty or null")
    @ValidPhoneNumber
    private String phoneNumber;
    @NotBlank(message = "birth date can't be empty or null")
    @Past(message = "birth date must be early than current day")
    @ValidAge
    private LocalDate birthdate;
}
