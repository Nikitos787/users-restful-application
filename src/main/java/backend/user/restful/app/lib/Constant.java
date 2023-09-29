package backend.user.restful.app.lib;

import backend.user.restful.app.dto.UserLoginRequestDto;
import backend.user.restful.app.dto.UserRegistrationRequestDto;
import backend.user.restful.app.dto.UserRequestDto;
import backend.user.restful.app.dto.UserResponseDto;
import backend.user.restful.app.model.Role;
import backend.user.restful.app.model.User;
import java.time.LocalDate;
import java.util.Set;

public class Constant {
    public static final String WRONG_EMAIL = "wrong@gmail.com";
    public static final String FIRST_NAME = "User";
    public static final String FIRST_NAME_FOR_SEARCH = "User";
    public static final String LAST_NAME = "Userenko";
    public static final String EMAIL = "user1@gmail.com";
    public static final String PASSWORD = "user1234";
    public static final LocalDate BIRTHDATE = LocalDate.of(1980, 1, 1);
    public static final String PHONE_NUMBER = "380987766543";
    public static final String ADDRESS = "SuperStreet1";
    public static final String ID_FIELD = "id";
    public static final String EMAIL_FIELD = "email";
    public static final String FIRST_NAME_FIELD = "firstName";
    public static final String LAST_NAME_FIELD = "lastName";
    public static final String BIRTHDATE_FIELD = "birthdate";
    public static final String ADDRESS_FIELD = "address";
    public static final String PHONE_NUMBER_FIELD = "phoneNumber";
    public static final String EXISTING_LOGIN = "user1@gmail.com";
    public static final String PASSWORD_FOR_LOGIN = "user1234";
    public static final String WORNG_PASSWORD = "djsdjjksdjksdkdsk";
    public static final String USER_ROLE = "USER";
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String REQUEST_FIRST_NAME = "Nikitosik";
    public static final String REQUEST_LAST_NAME = "Nikitenko";
    public static final String REQUEST_EMAIL = "useruseruser@gmail.com";
    public static final String REQUEST_PASSWORD = "user12121212";
    public static final LocalDate REQUEST_BIRTHDATE = LocalDate.of(1991, 1, 1);
    public static final String REQUEST_PHONE_NUMBER = "380969767928";
    public static final String REQUEST_ADDRESS = "Nova Street 11";
    public static final Integer EXPECTED_LENGTH = 3;
    public static final Integer EXPECTED_LENGTH_FOR_REPO = 2;
    public static final Integer EXPECTED_LENGTH_FOR_REQUEST_FIND_BY_BIRTHDATE_BETWEEN = 2;
    public static final Integer RESPONSE_POSITION = 0;
    public static final String FROM_PARAM = "from";
    public static final String TO_PARAM = "to";
    public static final String VALUE_FROM = "1980-01-01";
    public static final String WRONG_VALUE_FROM = "2001-01-01";
    public static final String VALUE_TO = "2000-01-01";
    public static final String JSON_REQUEST =
            "[{\"op\":\"replace\",\"path\":\"/email\",\"value\":\"ususduusdusd@gmail.com\"}]\n";
    public static final String APPLICATION_JSON_PATCH_CONTENT_TYPE = "application/json-patch+json";
    public static final String FIRST_DATE = "1980-01-01";
    public static final String SECOND_DATE = "1989-01-01";
    public static final Long EXPECTED_LENGTH_FOR_ALL_USER_IN_TEST_DB = 3L;
    public static final Long ID = 1L;
    public static final Long WRONG_ID = 1000L;
    public static final Long ID_FOR_USER_BY_EMAIL = 3L;
    public static final String EMAIL_FOR_SEARCH = "user3@gmail.com";
    public static final String WRONG_EMAIL_FOR_SEARCH = "someemail.com";
    public static final Integer PAGE_NUMBER = 0;
    public static final Integer PAGE_SIZE = 10;
    public static final String JWT_TOKEN = "jwtToken";
    public static final String HASHED_PASSWORD = "hashedPassword";
    public static final Integer EXPECTED_SIZE = 1;
    public static final LocalDate FROM = LocalDate.of(1990, 1, 1);
    public static final LocalDate TO = LocalDate.of(1991, 1, 1);
    public static final Integer ONE_TIME = 1;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_TYPE = "Bearer ";
    public static final int HEAD = 7;
    public static final String ROLES_TABLE = "roles";
    public static final String PHONE_NUMBER_PATTERN = "[0-9]{9}$";
    public static final String FIRST_SYMBOLS = "^";
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String USER = "ROLE_USER";
    public static final String ADMIN = "ROLE_ADMIN";

    public static UserRegistrationRequestDto getUserRegistrationRequestDto() {
        UserRegistrationRequestDto userRegistrationRequestDto = new UserRegistrationRequestDto();
        userRegistrationRequestDto.setFirstName(FIRST_NAME);
        userRegistrationRequestDto.setLastName(LAST_NAME);
        userRegistrationRequestDto.setEmail(EMAIL);
        userRegistrationRequestDto.setPassword(PASSWORD);
        userRegistrationRequestDto.setRepeatPassword(PASSWORD);
        userRegistrationRequestDto.setBirthdate(BIRTHDATE);
        userRegistrationRequestDto.setPhoneNumber(PHONE_NUMBER);
        userRegistrationRequestDto.setAddress(ADDRESS);
        return userRegistrationRequestDto;
    }

    public static UserResponseDto getUserResponseDto() {
        UserResponseDto expected = new UserResponseDto();
        expected.setId(ID);
        expected.setFirstName(FIRST_NAME);
        expected.setLastName(LAST_NAME);
        expected.setEmail(EMAIL);
        expected.setAddress(ADDRESS);
        expected.setBirthdate(BIRTHDATE);
        expected.setPhoneNumber(PHONE_NUMBER);
        expected.setRolesName(Set.of(Role.RoleName.ROLE_USER.name()));
        return expected;
    }

    public static UserLoginRequestDto getuserLoginRequestDto() {
        UserLoginRequestDto userLoginRequestDto = new UserLoginRequestDto();
        userLoginRequestDto.setLogin(EXISTING_LOGIN);
        userLoginRequestDto.setPassword(PASSWORD_FOR_LOGIN);
        return userLoginRequestDto;
    }

    public static UserRequestDto getUserRequestDto() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName(REQUEST_FIRST_NAME);
        userRequestDto.setLastName(REQUEST_LAST_NAME);
        userRequestDto.setEmail(REQUEST_EMAIL);
        userRequestDto.setPassword(REQUEST_PASSWORD);
        userRequestDto.setAddress(REQUEST_ADDRESS);
        userRequestDto.setBirthdate(REQUEST_BIRTHDATE);
        userRequestDto.setPhoneNumber(REQUEST_PHONE_NUMBER);
        return userRequestDto;
    }

    public static Role getTestRole() {
        Role role = new Role();
        role.setId(ID);
        role.setRoleName(Role.RoleName.ROLE_USER);
        role.setDeleted(false);
        return role;
    }

    public static User getTestUser() {
        User user = new User();
        user.setId(ID);
        user.setFirstName(Constant.FIRST_NAME);
        user.setLastName(Constant.LAST_NAME);
        user.setEmail(Constant.EMAIL);
        user.setPassword(Constant.PASSWORD);
        user.setBirthdate(Constant.BIRTHDATE);
        user.setPhoneNumber(Constant.PHONE_NUMBER);
        user.setAddress(Constant.ADDRESS);
        return user;
    }
}
