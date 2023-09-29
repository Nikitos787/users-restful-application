package back.controller;

import back.dto.UserLoginRequestDto;
import back.dto.UserLoginResponseDto;
import back.dto.UserRegistrationRequestDto;
import back.dto.UserResponseDto;
import back.exception.RegistrationException;
import back.security.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication management", description = "endpoints for managing authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "endpoint for authentication")
    public UserLoginResponseDto login(@RequestBody
                                      @Valid
                                      @Parameter(schema =
                                      @Schema(implementation = UserLoginRequestDto.class))
                                      UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }

    @PostMapping("/registration")
    @Operation(summary = "endpoint for registration new user")
    public UserResponseDto register(@RequestBody
                                    @Valid
                                    @Parameter(schema =
                                    @Schema(implementation = UserRegistrationRequestDto.class))
                                        UserRegistrationRequestDto request)
            throws RegistrationException {
        return authenticationService.register(request);
    }
}
