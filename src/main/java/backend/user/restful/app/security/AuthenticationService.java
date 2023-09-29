package backend.user.restful.app.security;

import backend.user.restful.app.dto.UserLoginRequestDto;
import backend.user.restful.app.dto.UserLoginResponseDto;
import backend.user.restful.app.dto.UserRegistrationRequestDto;
import backend.user.restful.app.dto.UserResponseDto;
import backend.user.restful.app.exception.RegistrationException;
import backend.user.restful.app.model.User;
import backend.user.restful.app.service.UserService;
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
