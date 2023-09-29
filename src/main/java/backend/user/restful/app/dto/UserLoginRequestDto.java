package backend.user.restful.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    @NotBlank(message = "login can't be null or empty")
    private String login;
    @NotBlank(message = "password can't be null or empty")
    private String password;
}
