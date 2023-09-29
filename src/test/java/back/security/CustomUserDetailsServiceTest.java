package back.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import back.model.Role;
import back.model.User;
import back.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    private static final String EMAIL = "bob@i.ua";
    private static final String WRONG_EMAIL = "wrong@gmail.com";
    private static final String PASSWORD = "111111111";
    private static final Long ID = 1L;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;
    @Mock
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(ID);
        role.setRoleName(Role.RoleName.ROLE_USER);
        user = new User();
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setRoles(Set.of(role));
    }

    @Test
    @DisplayName("test for successful getting user")
    void loadUserByUsername_ShouldReturnUser_Ok() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.ofNullable(user));

        UserDetails actual = customUserDetailsService.loadUserByUsername(EMAIL);
        assertNotNull(actual);
        assertEquals(EMAIL, actual.getUsername());
        assertEquals(PASSWORD, actual.getPassword());
    }

    @Test
    @DisplayName("test for throwing exception when user not found")
    void loadUserByUsername_UserNotFoundException_Throw_Not_Ok() {
        when(userRepository.findByEmail(WRONG_EMAIL)).thenReturn(Optional.empty());
        try {
            customUserDetailsService.loadUserByUsername(WRONG_EMAIL);
        } catch (UsernameNotFoundException e) {
            assertEquals("Can't find user int db by username: " + WRONG_EMAIL, e.getMessage());
        }
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(EMAIL);
        }, "UsernameNotFoundException expected");
    }
}