package back.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import javax.sql.DataSource;
import back.dto.UserRequestDto;
import back.dto.UserResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    private static final String USER_ROLE = "USER";
    private static final String ADMIN_ROLE = "ADMIN";
    private static final Long USER_EXPECTED_ID = 1L;
    private static final String FIRST_NAME = "User";
    private static final String LAST_NAME = "Userneko";
    private static final String EMAIL = "user1@gmail.com";
    private static final LocalDate BIRTHDATE = LocalDate.of(1980, 1, 1);
    private static final String PHONE_NUMBER = "380987766543";
    private static final String ADDRESS = "SuperStreet1";
    private static final String REQUEST_FIRST_NAME = "Nikitosik";
    private static final String REQUEST_LAST_NAME = "Nikitenko";
    private static final String REQUEST_EMAIL = "useruseruser@gmail.com";
    private static final String REQUEST_PASSWORD = "user12121212";
    private static final LocalDate REQUEST_BIRTHDATE = LocalDate.of(1991, 1, 1);
    private static final String REQUEST_PHONE_NUMBER = "380969767928";
    private static final String REQUEST_ADDRESS = "Nova Street 11";
    private static final String ID_FIELD = "id";
    private static final String EMAIL_FIELD = "email";
    private static final String FIRST_NAME_FIELD = "firstName";
    private static final String LAST_NAME_FIELD = "lastName";
    private static final String BIRTHDATE_FIELD = "birthdate";
    private static final String ADDRESS_FIELD = "address";
    private static final String PHONE_NUMBER_FIELD = "phoneNumber";
    private static final String EXISTING_LOGIN = "user1@gmail.com";
    private static final Integer EXPECTED_LENGTH = 3;
    private static final Integer EXPECTED_LENGTH_FOR_REQUEST_FIND_BY_BIRTHDATE_BETWEEN = 2;
    private static final Integer RESPONSE_POSITION = 0;
    private static final String FROM_PARAM = "from";
    private static final String TO_PARAM = "to";
    private static final String VALUE_FROM = "1980-01-01";
    private static final String WRONG_VALUE_FROM = "2001-01-01";
    private static final String VALUE_TO = "2000-01-01";
    private static final String JSON_REQUEST = "[{\"op\":\"replace\",\"path\":\"/email\",\"value\":\"ususduusdusd@gmail.com\"}]\n";
    private static final String APPLICATION_JSON_PATCH_CONTENT_TYPE = "application/json-patch+json";
    protected static MockMvc mockMvc;

    private UserResponseDto expected;
    private UserRequestDto userRequestDto;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) {
        resetDb(dataSource);
    }

    @SneakyThrows
    static void resetDb(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/restore-db.sql")
            );
        }
    }

    @BeforeEach
    void setUp(@Autowired DataSource dataSource) throws SQLException {
        expected = new UserResponseDto();
        expected.setId(USER_EXPECTED_ID);
        expected.setFirstName(FIRST_NAME);
        expected.setLastName(LAST_NAME);
        expected.setEmail(EMAIL);
        expected.setBirthdate(BIRTHDATE);
        expected.setPhoneNumber(PHONE_NUMBER);
        expected.setAddress(ADDRESS);
        tearDown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/initialize-db.sql")
            );
        }
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
    @WithMockUser(roles = USER_ROLE)
    @DisplayName("test for successful getting all users")
    public void findAll_ShouldReturnAll3Users_Ok() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto[] actual = objectMapper.readValue(result.getResponse().getContentAsByteArray(), UserResponseDto[].class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(EXPECTED_LENGTH, actual.length);
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    @DisplayName("test for successful getting user by id by Admin")
    public void findById_ShouldReturnUSerResponseById_Ok() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/api/users/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponseDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, ID_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual, EMAIL_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual, FIRST_NAME_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual, LAST_NAME_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual, ADDRESS_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual, BIRTHDATE_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual, PHONE_NUMBER_FIELD);
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    @DisplayName("test for throwing exception when try to get not user by not existing id")
    public void findById_returnNotExistingUserResponseById_Not_Ok() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/api/users/{id}", 4)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        assertThat(jsonResponse, (jsonResponse).contains("Can't find user by user id: 4"));
    }

    @Test
    @WithMockUser(roles = USER_ROLE)
    @DisplayName("test for throwing exception when try to get not user by not existing id by not admin")
    public void findById_returnNotExistingUserResponseByIdForUserWithoutAuthorities_Not_Ok() throws Exception {
        mockMvc.perform(
                        get("/api/users/{id}", 4)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    @DisplayName("test for deleting user by id by admin")
    public void deleteById_ShouldDeleteUserById_Ok() throws Exception {
        mockMvc.perform(
                        delete("/api/users/{id}", 3)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = USER_ROLE)
    @DisplayName("test for throwing exception when you not admin and try delete user")
    public void deleteById_DeleteUserByIdByUserWithoutAuthorities_Not_Ok() throws Exception {
        mockMvc.perform(
                        delete("/api/users/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = USER_ROLE)
    @DisplayName("test for getting users by params")
    public void searchUsers_ShouldFindUserByParams_Ok() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/users/search")
                                .param("firstNames", FIRST_NAME)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        UserResponseDto[] actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsByteArray(),
                UserResponseDto[].class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual[RESPONSE_POSITION], ID_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual[RESPONSE_POSITION], EMAIL_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual[RESPONSE_POSITION], FIRST_NAME_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual[RESPONSE_POSITION], LAST_NAME_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual[RESPONSE_POSITION], ADDRESS_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual[RESPONSE_POSITION], BIRTHDATE_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual[RESPONSE_POSITION], PHONE_NUMBER_FIELD);
    }

    @Test
    @WithMockUser(roles = USER_ROLE)
    @DisplayName("test for getting users by date range")
    public void searchByDate_ShouldFindUserByBirthDayBetween_Ok() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/users/search-by-date-between")
                                .param(FROM_PARAM, VALUE_FROM)
                                .param(TO_PARAM, VALUE_TO)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        UserResponseDto[] actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsByteArray(),
                UserResponseDto[].class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(EXPECTED_LENGTH_FOR_REQUEST_FIND_BY_BIRTHDATE_BETWEEN, actual.length);
        EqualsBuilder.reflectionEquals(expected, actual[RESPONSE_POSITION], ID_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual[RESPONSE_POSITION], EMAIL_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual[RESPONSE_POSITION], FIRST_NAME_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual[RESPONSE_POSITION], LAST_NAME_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual[RESPONSE_POSITION], ADDRESS_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual[RESPONSE_POSITION], BIRTHDATE_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual[RESPONSE_POSITION], PHONE_NUMBER_FIELD);
    }

    @Test
    @WithMockUser(roles = USER_ROLE)
    @DisplayName("test for getting user when from param bigger than to param. Should throw exception")
    public void searchByDate_findUserByBirthDayBetweenIfFromBiggerThanTo_Not_Ok() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/users/search-by-date-between")
                                .param(FROM_PARAM, WRONG_VALUE_FROM)
                                .param(TO_PARAM, VALUE_TO)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertThat(jsonResponse, (jsonResponse).contains("From date must be less than or equal to To date"));
    }

    @Test
    @WithMockUser(roles = USER_ROLE, username = EXISTING_LOGIN)
    @DisplayName("test for updating all your fields")
    public void updateAllInfo_ShouldUpdateAllField_Ok() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(userRequestDto);
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/users/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserResponseDto actual = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), UserResponseDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(userRequestDto, actual, EMAIL_FIELD);
        EqualsBuilder.reflectionEquals(userRequestDto, actual, FIRST_NAME);
        EqualsBuilder.reflectionEquals(userRequestDto, actual, LAST_NAME_FIELD);
        EqualsBuilder.reflectionEquals(userRequestDto, actual, ADDRESS_FIELD);
        EqualsBuilder.reflectionEquals(userRequestDto, actual, BIRTHDATE_FIELD);
        EqualsBuilder.reflectionEquals(userRequestDto, actual, PHONE_NUMBER_FIELD);
    }

    @Test
    @WithMockUser(roles = USER_ROLE, username = EXISTING_LOGIN)
    @DisplayName("test throwing exception when you update all not your fields")
    public void updateAllInfo_updateAllFieldByNotYourself_Not_Ok() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(userRequestDto);
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/users/{id}", 2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertThat(jsonResponse, (jsonResponse).contains("Cannot update another user's info"));

    }

    @Test
    @WithMockUser(roles = USER_ROLE, username = EXISTING_LOGIN)
    @DisplayName("test for update one or some your fields")
    public void patch_ShouldPatch_Ok() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/{id}", 1)
                                .contentType(APPLICATION_JSON_PATCH_CONTENT_TYPE)
                                .content(JSON_REQUEST)
                )
                .andExpect(status().isOk())
                .andReturn();
        UserResponseDto actual = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), UserResponseDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        String expectedEmail = "ususduusdusd@gmail.com";
        Assertions.assertEquals(expectedEmail, actual.getEmail());
        EqualsBuilder.reflectionEquals(userRequestDto, actual, FIRST_NAME_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual, LAST_NAME_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual, ADDRESS_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual, BIRTHDATE_FIELD);
        EqualsBuilder.reflectionEquals(expected, actual, PHONE_NUMBER_FIELD);
    }
}
