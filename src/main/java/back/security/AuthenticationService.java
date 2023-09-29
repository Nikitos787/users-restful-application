package back.security;

import back.dto.UserLoginRequestDto;
import back.dto.UserLoginResponseDto;
import back.dto.UserRegistrationRequestDto;
import back.dto.UserResponseDto;
import back.exception.RegistrationException;
import back.model.User;
import back.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserResponseDto register(
            UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException {
        return userService.save(userRegistrationRequestDto);
    }

    public UserLoginResponseDto authenticate(
            UserLoginRequestDto authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getLogin(),
                        authenticationRequest.getPassword())
        );
        User user = userService.findByEmail(authenticationRequest.getLogin());
        String jwtToken = jwtService.generateToken(user);
        return new UserLoginResponseDto(jwtToken);
    }
}
