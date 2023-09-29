package back.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import back.dto.BirthDateBetweenSearchParametersDto;
import back.dto.UserRegistrationRequestDto;
import back.dto.UserRequestDto;
import back.dto.UserResponseDto;
import back.dto.UserSearchParametersDto;
import back.exception.EntityNotFoundException;
import back.exception.RegistrationException;
import back.mapper.UserMapper;
import back.model.Role;
import back.model.User;
import back.repository.SpecificationBuilder;
import back.repository.user.UserRepository;
import back.service.RoleService;
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
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String EMAIL = "johndoe@example.com";
    private static final String PASSWORD = "password";
    private static final LocalDate BIRTHDATE = LocalDate.of(1990, 1, 1);
    private static final String PHONE_NUMBER = "380977676655";
    private static final String ADDRESS = "Lodsoodsods 1";
    private static final String HASHED_PASSWORD = "hashedPassword";
    private static final Long ID = 1L;
    private static final Integer EXPECTED_SIZE = 1;
    private static final String ID_FIELD = "id";
    private static final String EMAIL_FIELD = "email";
    private static final String FIRST_NAME_FIELD = "firstName";
    private static final String LAST_NAME_FIELD = "lastName";
    private static final String BIRTHDATE_FIELD = "birthdate";
    private static final String ADDRESS_FIELD = "address";
    private static final String PHONE_NUMBER_FIELD = "phoneNumber";
    private static final Integer PAGE_NUMBER = 0;
    private static final Integer PAGE_SIZE = 10;
    private static final LocalDate FROM = LocalDate.of(1990, 1, 1);
    private static final LocalDate TO = LocalDate.of(1991, 1, 1);
    private static final String REQUEST_FIRST_NAME = "Nikitosik";
    private static final String REQUEST_LAST_NAME = "Nikitenko";
    private static final String REQUEST_EMAIL = "useruseruser@gmail.com";
    private static final String REQUEST_PASSWORD = "user12121212";
    private static final LocalDate REQUEST_BIRTHDATE = LocalDate.of(1991, 1, 1);
    private static final String REQUEST_PHONE_NUMBER = "380969767928";
    private static final String REQUEST_ADDRESS = "Nova Street 11";
    private static final Integer ONE_TIME = 1;

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
        userRegistrationRequestDto = new UserRegistrationRequestDto();
        userRegistrationRequestDto.setFirstName(FIRST_NAME);
        userRegistrationRequestDto.setLastName(LAST_NAME);
        userRegistrationRequestDto.setEmail(EMAIL);
        userRegistrationRequestDto.setPassword(PASSWORD);
        userRegistrationRequestDto.setRepeatPassword(PASSWORD);
        userRegistrationRequestDto.setBirthdate(BIRTHDATE);
        userRegistrationRequestDto.setPhoneNumber(PHONE_NUMBER);
        userRegistrationRequestDto.setAddress(ADDRESS);

        role = new Role();
        role.setId(ID);
        role.setRoleName(Role.RoleName.ROLE_USER);
        role.setDeleted(false);

        user = new User();
        user.setId(ID);
        user.setFirstName(userRegistrationRequestDto.getFirstName());
        user.setLastName(userRegistrationRequestDto.getLastName());
        user.setEmail(userRegistrationRequestDto.getEmail());
        user.setPassword(userRegistrationRequestDto.getPassword());
        user.setBirthdate(userRegistrationRequestDto.getBirthdate());
        user.setPhoneNumber(userRegistrationRequestDto.getPhoneNumber());
        user.setAddress(userRegistrationRequestDto.getAddress());
        user.setRoles(Set.of(role));

        userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setBirthdate(user.getBirthdate());
        userResponseDto.setAddress(user.getAddress());
        userResponseDto.setPhoneNumber(user.getPhoneNumber());
        userResponseDto.setRolesName(
                user.getRoles().stream()
                        .map(r -> r.getRoleName().name())
                        .collect(Collectors.toSet())
        );
        userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName(REQUEST_FIRST_NAME);
        userRequestDto.setLastName(REQUEST_LAST_NAME);
        userRequestDto.setEmail(REQUEST_EMAIL);
        userRequestDto.setPassword(REQUEST_PASSWORD);
        userRequestDto.setAddress(REQUEST_ADDRESS);
        userRequestDto.setBirthdate(REQUEST_BIRTHDATE);
        userRequestDto.setPhoneNumber(REQUEST_PHONE_NUMBER);
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
        EqualsBuilder.reflectionEquals(userResponseDto, actual, FIRST_NAME_FIELD);
        EqualsBuilder.reflectionEquals(userResponseDto, actual, LAST_NAME_FIELD);
        EqualsBuilder.reflectionEquals(userResponseDto, actual, EMAIL_FIELD);
        EqualsBuilder.reflectionEquals(userResponseDto, actual, ADDRESS_FIELD);
        EqualsBuilder.reflectionEquals(userResponseDto, actual, PHONE_NUMBER_FIELD);
        EqualsBuilder.reflectionEquals(userResponseDto, actual, BIRTHDATE_FIELD);
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
        EqualsBuilder.reflectionEquals(userResponseDto, actual, FIRST_NAME_FIELD);
        EqualsBuilder.reflectionEquals(userResponseDto, actual, LAST_NAME_FIELD);
        EqualsBuilder.reflectionEquals(userResponseDto, actual, EMAIL_FIELD);
        EqualsBuilder.reflectionEquals(userResponseDto, actual, ADDRESS_FIELD);
        EqualsBuilder.reflectionEquals(userResponseDto, actual, PHONE_NUMBER_FIELD);
        EqualsBuilder.reflectionEquals(userResponseDto, actual, BIRTHDATE_FIELD);
    }

    @Test
    @DisplayName("test for successful getting user by email")
    void findByEmail_NotSuccessfulGettingUser_Not_Ok() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        User actual = userService.findByEmail(EMAIL);
        EqualsBuilder.reflectionEquals(userResponseDto, actual, ID_FIELD);
        EqualsBuilder.reflectionEquals(userResponseDto, actual, FIRST_NAME_FIELD);
        EqualsBuilder.reflectionEquals(userResponseDto, actual, LAST_NAME_FIELD);
        EqualsBuilder.reflectionEquals(userResponseDto, actual, EMAIL_FIELD);
        EqualsBuilder.reflectionEquals(userResponseDto, actual, ADDRESS_FIELD);
        EqualsBuilder.reflectionEquals(userResponseDto, actual, PHONE_NUMBER_FIELD);
        EqualsBuilder.reflectionEquals(userResponseDto, actual, BIRTHDATE_FIELD);
    }

    @Test
    @DisplayName("test for unsuccessful getting user by email")
    void findByEmail_NotSuccessfullGettingUserByEmail_NotOk() {
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
    void updateAllFields_SHouldUpdateAllFields_Ok() throws RegistrationException {
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);
        when(userRepository.findByEmail(REQUEST_EMAIL))
                .thenReturn(Optional.empty());
        when(userRepository.findById(ID)).thenReturn(Optional.of(user));

        UserResponseDto actual = userService.updateInfo(user.getId(), user, userRequestDto);
        assertNotNull(actual);
        assertEquals(userResponseDto.getId(), actual.getId());
    }

    @Test
    @DisplayName("test for void delete")
    void delete_ShouldDeleteUser_Ok() {
        userService.delete(ID);
        verify(userRepository, times(ONE_TIME)).deleteById(anyLong());
    }
}
