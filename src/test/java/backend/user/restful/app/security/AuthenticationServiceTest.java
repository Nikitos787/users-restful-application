package backend.user.restful.app.security;

import static backend.user.restful.app.lib.Constant.ID_FIELD;
import static backend.user.restful.app.lib.Constant.JWT_TOKEN;
import static backend.user.restful.app.lib.Constant.getTestRole;
import static backend.user.restful.app.lib.Constant.getTestUser;
import static backend.user.restful.app.lib.Constant.getUserRegistrationRequestDto;
import static backend.user.restful.app.lib.Constant.getUserResponseDto;
import static backend.user.restful.app.lib.Constant.getuserLoginRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;
import backend.user.restful.app.dto.UserLoginRequestDto;
import backend.user.restful.app.dto.UserLoginResponseDto;
import backend.user.restful.app.dto.UserRegistrationRequestDto;
import backend.user.restful.app.dto.UserResponseDto;
import backend.user.restful.app.exception.RegistrationException;
import backend.user.restful.app.model.Role;
import backend.user.restful.app.model.User;
import backend.user.restful.app.service.UserService;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private UserRegistrationRequestDto userRegistrationRequestDto;
    private UserResponseDto userResponseDto;
    private UserLoginRequestDto authenticationRequest;
    private User user;


    @BeforeEach
    void setUp() {
        userRegistrationRequestDto = getUserRegistrationRequestDto();
        userResponseDto = getUserResponseDto();
        authenticationRequest = getuserLoginRequestDto();
        Role role = getTestRole();
        user = getTestUser();
        user.setRoles(Set.of(role));
    }

    @Test
    @DisplayName("test for successful registration case")
    void register_ShouldRegisterNewUser_Ok() throws RegistrationException {
        when(userService.save(userRegistrationRequestDto)).thenReturn(userResponseDto);

        UserResponseDto response = authenticationService.register(userRegistrationRequestDto);

        assertNotNull(response.getId());
        assertEquals(response.getId(), userResponseDto.getId());
        assertEquals(response.getFirstName(), userResponseDto.getFirstName());
        assertEquals(response.getLastName(), userResponseDto.getLastName());
        assertEquals(response.getBirthdate(), userResponseDto.getBirthdate());
        assertEquals(response.getAddress(), userResponseDto.getAddress());
        assertEquals(response.getEmail(), userResponseDto.getEmail());
        assertEquals(response.getPhoneNumber(), userResponseDto.getPhoneNumber());
        EqualsBuilder.reflectionEquals(response,userResponseDto, ID_FIELD);
    }

    @Test
    @DisplayName("test for throwing registration exception")
    void register_ShouldNotRegister_NotOk() throws RegistrationException {
        when(userService.save(userRegistrationRequestDto)).thenThrow(RegistrationException.class);
        assertThrows(RegistrationException.class, () -> {
            authenticationService.register(userRegistrationRequestDto);
        }, "RegistrationException expected");
    }

    @Test
    @DisplayName("test for successful authentication")
    void authenticate_ShouldLetUserToLogin_Ok() {
        UserLoginResponseDto expectedResponse = new UserLoginResponseDto(JWT_TOKEN);
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userService.findByEmail(authenticationRequest.getLogin()))
                .thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn(JWT_TOKEN);

        UserLoginResponseDto response = authenticationService.authenticate(authenticationRequest);

        assertNotNull(response);
        assertEquals(expectedResponse.getToken(), response.getToken());
    }

    @Test
    @DisplayName("test for throw exception while you try login with not existing credentials in db")
    void authenticate_UserNotFound_Not_Ok() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(UsernameNotFoundException.class);

        assertThrows(UsernameNotFoundException.class, () -> {
            authenticationService.authenticate(authenticationRequest);
        });
    }
}
