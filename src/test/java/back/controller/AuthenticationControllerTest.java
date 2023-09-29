package back.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Set;
import javax.sql.DataSource;
import back.dto.UserLoginRequestDto;
import back.dto.UserRegistrationRequestDto;
import back.dto.UserResponseDto;
import back.model.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {
    protected static MockMvc mockMvc;
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String EMAIL = "johndoe@example.com";
    private static final String PASSWORD = "password";
    private static final LocalDate BIRTHDATE = LocalDate.of(1990, 1, 1);
    private static final String PHONE_NUMBER = "380977676655";
    private static final String ADDRESS = "Lodsoodsods 1";
    private static final String ID_FIELD = "id";
    private static final String EMAIL_FIELD = "email";
    private static final String FIRST_NAME_FIELD = "firstName";
    private static final String LAST_NAME_FIELD = "lastName";
    private static final String BIRTHDATE_FIELD = "birthdate";
    private static final String ADDRESS_FIELD = "address";
    private static final String PHONE_NUMBER_FIELD = "phoneNumber";
    private static final String EXISTING_LOGIN = "user1@gmail.com";
    private static final String PASSWORD_FOR_LOGIN = "user1234";
    private static final Long USER_ID = 3L;
    private static final String WORNG_PASSWORD = "djsdjjksdjksdkdsk";

    private UserRegistrationRequestDto userRegistrationRequestDto;
    private UserResponseDto expected;
    private UserLoginRequestDto userLoginRequestDto;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource,
                          @Autowired WebApplicationContext applicationContext) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        tearDown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/initialize-db.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @SneakyThrows
    static void tearDown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/restore-db.sql")
            );
        }
    }

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

        expected = new UserResponseDto();
        expected.setId(USER_ID);
        expected.setFirstName(userRegistrationRequestDto.getFirstName());
        expected.setLastName(userRegistrationRequestDto.getLastName());
        expected.setEmail(userRegistrationRequestDto.getEmail());
        expected.setAddress(userRegistrationRequestDto.getAddress());
        expected.setBirthdate(userRegistrationRequestDto.getBirthdate());
        expected.setPhoneNumber(userRegistrationRequestDto.getPhoneNumber());
        expected.setRolesName(Set.of(Role.RoleName.ROLE_USER.name()));

        userLoginRequestDto = new UserLoginRequestDto();
        userLoginRequestDto.setLogin(EXISTING_LOGIN);
        userLoginRequestDto.setPassword(PASSWORD_FOR_LOGIN);
    }

    @Test
    @DisplayName("test for successful registration")
    public void register_SuccessfulCase_ok() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(userRegistrationRequestDto);
        MvcResult result = mockMvc.perform(
                        post("/api/auth/registration")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), UserResponseDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, ID_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual, EMAIL_FIELD);
        EqualsBuilder.reflectionEquals(expected, expected, FIRST_NAME_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual, LAST_NAME_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual, ADDRESS_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual, BIRTHDATE_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual, PHONE_NUMBER_FIELD);
    }

    @Test
    @DisplayName("test for unsuccessful registration")
    public void register_UnSuccessfulCase_notOk() throws Exception {
        userRegistrationRequestDto.setEmail(EXISTING_LOGIN);
        String jsonRequest = objectMapper.writeValueAsString(userRegistrationRequestDto);
        mockMvc.perform(
                        post("/api/auth/registration")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @DisplayName("test for successful authentication")
    public void login_SuccessfulCase_ok() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(userLoginRequestDto);
        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("test for unsuccessful registration")
    public void login_UnSuccessfulCase_notOk() throws Exception {
        userLoginRequestDto.setPassword(WORNG_PASSWORD);
        String jsonRequest = objectMapper.writeValueAsString(userLoginRequestDto);
        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }
}
