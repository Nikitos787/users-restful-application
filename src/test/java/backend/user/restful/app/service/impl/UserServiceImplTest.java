package backend.user.restful.app.service.impl;

import static backend.user.restful.app.lib.Constant.ADDRESS;
import static backend.user.restful.app.lib.Constant.ADDRESS_FIELD;
import static backend.user.restful.app.lib.Constant.BIRTHDATE;
import static backend.user.restful.app.lib.Constant.BIRTHDATE_FIELD;
import static backend.user.restful.app.lib.Constant.EMAIL;
import static backend.user.restful.app.lib.Constant.EMAIL_FIELD;
import static backend.user.restful.app.lib.Constant.EXPECTED_SIZE;
import static backend.user.restful.app.lib.Constant.FIRST_NAME;
import static backend.user.restful.app.lib.Constant.FIRST_NAME_FIELD;
import static backend.user.restful.app.lib.Constant.FROM;
import static backend.user.restful.app.lib.Constant.HASHED_PASSWORD;
import static backend.user.restful.app.lib.Constant.ID;
import static backend.user.restful.app.lib.Constant.ID_FIELD;
import static backend.user.restful.app.lib.Constant.LAST_NAME;
import static backend.user.restful.app.lib.Constant.LAST_NAME_FIELD;
import static backend.user.restful.app.lib.Constant.ONE_TIME;
import static backend.user.restful.app.lib.Constant.PAGE_NUMBER;
import static backend.user.restful.app.lib.Constant.PAGE_SIZE;
import static backend.user.restful.app.lib.Constant.PHONE_NUMBER;
import static backend.user.restful.app.lib.Constant.PHONE_NUMBER_FIELD;
import static backend.user.restful.app.lib.Constant.REQUEST_EMAIL;
import static backend.user.restful.app.lib.Constant.TO;
import static backend.user.restful.app.lib.Constant.getTestRole;
import static backend.user.restful.app.lib.Constant.getTestUser;
import static backend.user.restful.app.lib.Constant.getUserRegistrationRequestDto;
import static backend.user.restful.app.lib.Constant.getUserRequestDto;
import static backend.user.restful.app.lib.Constant.getUserResponseDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.user.restful.app.dto.BirthDateBetweenSearchParametersDto;
import backend.user.restful.app.dto.UserRegistrationRequestDto;
import backend.user.restful.app.dto.UserRequestDto;
import backend.user.restful.app.dto.UserResponseDto;
import backend.user.restful.app.dto.UserSearchParametersDto;
import backend.user.restful.app.exception.EntityNotFoundException;
import backend.user.restful.app.exception.RegistrationException;
import backend.user.restful.app.mapper.UserMapper;
import backend.user.restful.app.model.Role;
import backend.user.restful.app.model.User;
import backend.user.restful.app.repository.SpecificationBuilder;
import backend.user.restful.app.repository.user.UserRepository;
import backend.user.restful.app.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private Specification<User> specification;
    @Mock
    private SpecificationBuilder<User> specificationBuilder;
    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationRequestDto userRegistrationRequestDto;
    private Role role;
    private User user;
    private UserResponseDto userResponseDto;
    private UserRequestDto userRequestDto;

    @BeforeEach
    void setUp() {
        userRegistrationRequestDto = getUserRegistrationRequestDto();
        role = getTestRole();
        user = getTestUser();
        userResponseDto = getUserResponseDto();
        userRequestDto = getUserRequestDto();
    }

    @Test
    @DisplayName("test for successful save case")
    void save_SuccessfulSave_Ok() throws RegistrationException {
        when(userRepository.findByEmail(userRegistrationRequestDto.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(userRegistrationRequestDto.getPassword()))
                .thenReturn(HASHED_PASSWORD);
        when(userMapper.toModel(userRegistrationRequestDto)).thenReturn(user);
        when(roleService.findByRoleName(Role.RoleName.ROLE_USER)).thenReturn(role);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto actual = userService.save(userRegistrationRequestDto);

        EqualsBuilder.reflectionEquals(userResponseDto, actual, ID_FIELD);
    }

    @Test
    @DisplayName("test for unsuccessful save case")
    void save_NotSuccessfulSave_Not_Ok() {
        when(userRepository.findByEmail(userRegistrationRequestDto.getEmail()))
                .thenReturn(Optional.of(user));
        assertThrows(RegistrationException.class, () -> {
            userService.save(userRegistrationRequestDto);
        }, "RegistrationException expected");
    }

    @Test
    @DisplayName("test for getting users by params")
    void search_SuccessfulSearchByPrams_Ok() {
        List<User> users = List.of(user);
        UserSearchParametersDto userSearchParametersDto = new UserSearchParametersDto();
        userSearchParametersDto.setEmails(new String[]{EMAIL});
        userSearchParametersDto.setAddresses(new String[]{ADDRESS});
        userSearchParametersDto.setBirthdates(new LocalDate[]{BIRTHDATE});
        userSearchParametersDto.setLastNames(new String[]{LAST_NAME});
        userSearchParametersDto.setFirstNames(new String[]{FIRST_NAME});
        userSearchParametersDto.setPhoneNumbers(new String[]{PHONE_NUMBER});

        when(specificationBuilder.build(userSearchParametersDto)).thenReturn(specification);
        when(userRepository.findAll(specification)).thenReturn(users);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        List<UserResponseDto> actual = userService.search(userSearchParametersDto);
        assertEquals(EXPECTED_SIZE, actual.size());
    }

    @Test
    @DisplayName("test for successful getting user by id")
    void findById_SuccessfulGettingUser_Ok() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto actual = userService.findById(ID);
        EqualsBuilder.reflectionEquals(userResponseDto, actual, ID_FIELD);
    }

    @Test
    @DisplayName("test for successful getting user by email")
    void findByEmail_SuccessfulGettingUser_Ok() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        User actual = userService.findByEmail(EMAIL);
        EqualsBuilder.reflectionEquals(userResponseDto, actual, ID_FIELD);
    }

    @Test
    @DisplayName("test for unsuccessful getting user by email")
    void findByEmail_NotSuccessfulGettingUserByEmail_NotOk() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.findByEmail(EMAIL);
        }, "EntityNotFoundException expected");
    }

    @Test
    @DisplayName("test for getting all users")
    void findAll_SuccessfulGettingAllUsers_Ok() {
        List<User> users = List.of(user);
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        List<UserResponseDto> actual = userService.findAll(pageable);

        assertEquals(EXPECTED_SIZE, actual.size());
    }

    @Test
    @DisplayName("test for getting users bt date range")
    void searchByBirthdateBetween_ShouldReturnUsersByDateBetween_Ok() {
        BirthDateBetweenSearchParametersDto dto = new BirthDateBetweenSearchParametersDto();
        dto.setFrom(FROM);
        dto.setTo(TO);
        List<User> users = List.of(user);

        when(userRepository.findAllByBirthdateBetween(FROM, TO)).thenReturn(users);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        List<UserResponseDto> actual = userService.searchByBirthDateBetween(dto);
        assertEquals(EXPECTED_SIZE, actual.size());
    }

    @Test
    @DisplayName("test for update all fields")
    void updateAllFields_ShouldUpdateAllFields_Ok() throws RegistrationException {
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);
        when(userRepository.findByEmail(REQUEST_EMAIL))
                .thenReturn(Optional.empty());
        when(userRepository.findById(ID)).thenReturn(Optional.of(user));

        UserResponseDto actual = userService.updateInfo(user.getId(), user, userRequestDto);
        assertNotNull(actual);
        assertEquals(userResponseDto.getId(), actual.getId());
        EqualsBuilder.reflectionEquals(userResponseDto, actual, ID_FIELD);
    }

    @Test
    @DisplayName("test for void delete")
    void delete_ShouldDeleteUser_Ok() {
        userService.delete(ID);
        verify(userRepository, times(ONE_TIME)).deleteById(anyLong());
    }
}
