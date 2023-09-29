package backend.user.restful.app.security;

import static backend.user.restful.app.lib.Constant.EMAIL;
import static backend.user.restful.app.lib.Constant.PASSWORD;
import static backend.user.restful.app.lib.Constant.WRONG_EMAIL;
import static backend.user.restful.app.lib.Constant.getTestRole;
import static backend.user.restful.app.lib.Constant.getTestUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import backend.user.restful.app.model.Role;
import backend.user.restful.app.model.User;
import backend.user.restful.app.repository.user.UserRepository;
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
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        Role role = getTestRole();
        user = getTestUser();
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