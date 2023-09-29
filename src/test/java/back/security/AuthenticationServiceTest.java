package back.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Set;
import back.dto.UserLoginRequestDto;
import back.dto.UserLoginResponseDto;
import back.dto.UserRegistrationRequestDto;
import back.dto.UserResponseDto;
import back.exception.RegistrationException;
import back.model.Role;
import back.model.User;
import back.service.UserService;
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
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String EMAIL = "johndoe@example.com";
    private static final String PASSWORD = "password";
    private static final LocalDate BIRTHDATE = LocalDate.of(1990, 1, 1);
    private static final String PHONE_NUMBER = "380977676655";
    private static final String ADDRESS = "Lodsoodsods 1";
    private static final Long ID = 1L;
    private static final String JWT_TOKEN = "jwtToken";

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


    @BeforeEach
    void setUp() {
        userRegistrationRequestDto = new UserRegistrationRequestDto();
        userRegistrationRequestDto.setFirstName(FIRST_NAME);
        userRegistrationRequestDto.setLastName(LAST_NAME);
        userRegistrationRequestDto.setEmail(EMAIL);
        userRegistrationRequestDto.setPassword(PASSWORD);
        userRegistrationRequestDto.setRepeatPassword(PASSWORD);
        userRegistrationRequestDto.setBirthdate(BIRTHDATE);
        userRegistrationRequestDto.setPhoneNumber(PHONE_NUMBER);
        userRegistrationRequestDto.setAddress(ADDRESS);

        userResponseDto = new UserResponseDto();
        userResponseDto.setId(ID);
        userResponseDto.setFirstName(userRegistrationRequestDto.getFirstName());
        userResponseDto.setLastName(userResponseDto.getLastName());
        userResponseDto.setAddress(userRegistrationRequestDto.getAddress());
        userResponseDto.setBirthdate(userRegistrationRequestDto.getBirthdate());
        userResponseDto.setEmail(userRegistrationRequestDto.getEmail());
        userResponseDto.setPhoneNumber(userRegistrationRequestDto.getPhoneNumber());
        userResponseDto.setRolesName(Set.of(Role.RoleName.ROLE_USER.name()));
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
        UserLoginRequestDto authenticationRequest = new UserLoginRequestDto();
        authenticationRequest.setLogin(EMAIL);
        authenticationRequest.setPassword(PASSWORD);

        Role role = new Role();
        role.setId(ID);
        role.setRoleName(Role.RoleName.ROLE_USER);
        role.setDeleted(false);

        User user = new User();
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setBirthdate(BIRTHDATE);
        user.setPhoneNumber(PHONE_NUMBER);
        user.setAddress(ADDRESS);
        user.setRoles(Set.of(role));

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
        UserLoginRequestDto authenticationRequest = new UserLoginRequestDto();
        authenticationRequest.setLogin(EMAIL);
        authenticationRequest.setPassword(PASSWORD);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(UsernameNotFoundException.class);

        assertThrows(UsernameNotFoundException.class, () -> {
            authenticationService.authenticate(authenticationRequest);
        });
    }
}
