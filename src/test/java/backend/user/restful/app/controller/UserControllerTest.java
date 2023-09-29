package backend.user.restful.app.controller;

import static backend.user.restful.app.lib.Constant.ADDRESS_FIELD;
import static backend.user.restful.app.lib.Constant.ADMIN_ROLE;
import static backend.user.restful.app.lib.Constant.APPLICATION_JSON_PATCH_CONTENT_TYPE;
import static backend.user.restful.app.lib.Constant.BIRTHDATE_FIELD;
import static backend.user.restful.app.lib.Constant.EMAIL_FIELD;
import static backend.user.restful.app.lib.Constant.EXISTING_LOGIN;
import static backend.user.restful.app.lib.Constant.EXPECTED_LENGTH;
import static backend.user.restful.app.lib.Constant.EXPECTED_LENGTH_FOR_REQUEST_FIND_BY_BIRTHDATE_BETWEEN;
import static backend.user.restful.app.lib.Constant.FIRST_NAME_FIELD;
import static backend.user.restful.app.lib.Constant.FIRST_NAME_FOR_SEARCH;
import static backend.user.restful.app.lib.Constant.FROM_PARAM;
import static backend.user.restful.app.lib.Constant.ID_FIELD;
import static backend.user.restful.app.lib.Constant.JSON_REQUEST;
import static backend.user.restful.app.lib.Constant.LAST_NAME_FIELD;
import static backend.user.restful.app.lib.Constant.PHONE_NUMBER_FIELD;
import static backend.user.restful.app.lib.Constant.RESPONSE_POSITION;
import static backend.user.restful.app.lib.Constant.TO_PARAM;
import static backend.user.restful.app.lib.Constant.USER_ROLE;
import static backend.user.restful.app.lib.Constant.VALUE_FROM;
import static backend.user.restful.app.lib.Constant.VALUE_TO;
import static backend.user.restful.app.lib.Constant.WRONG_VALUE_FROM;
import static backend.user.restful.app.lib.Constant.getUserRequestDto;
import static backend.user.restful.app.lib.Constant.getUserResponseDto;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import backend.user.restful.app.dto.UserRequestDto;
import backend.user.restful.app.dto.UserResponseDto;
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
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
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
        expected = getUserResponseDto();
        userRequestDto = getUserRequestDto();
        tearDown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/initialize-db.sql")
            );
        }
    }

    @Test
    @WithMockUser(roles = USER_ROLE)
    @DisplayName("test for successful getting all users")
    public void findAll_ShouldReturnAll3Users_Ok() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/api/users")
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto[] actual = objectMapper
                .readValue(result.getResponse().getContentAsByteArray(), UserResponseDto[].class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(EXPECTED_LENGTH, actual.length);
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    @DisplayName("test for successful getting user by id by Admin")
    public void findById_ShouldReturnUSerResponseById_Ok() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/api/users/{id}", 1)
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), UserResponseDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.getLastName(), actual.getLastName());
        Assertions.assertEquals(expected.getAddress(), actual.getAddress());
        EqualsBuilder.reflectionEquals(expected, actual, ID_FIELD);
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    @DisplayName("test for throwing exception when try to get not user by not existing id")
    public void findById_returnNotExistingUserResponseById_Not_Ok() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/api/users/{id}", 4)
                                .contentType(APPLICATION_JSON)
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
                                .contentType(APPLICATION_JSON)
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
                                .contentType(APPLICATION_JSON)
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
                                .contentType(APPLICATION_JSON)
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
                                .param("firstNames", FIRST_NAME_FOR_SEARCH)
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        UserResponseDto[] actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsByteArray(),
                UserResponseDto[].class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.getLastName(), actual[RESPONSE_POSITION].getLastName());
        Assertions.assertEquals(expected.getAddress(), actual[RESPONSE_POSITION].getAddress());
        EqualsBuilder.reflectionEquals(expected, actual[RESPONSE_POSITION], ID_FIELD);

    }

    @Test
    @WithMockUser(roles = USER_ROLE)
    @DisplayName("test for getting users by date range")
    public void searchByDate_ShouldFindUserByBirthDayBetween_Ok() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/users/search-by-date-between")
                                .param(FROM_PARAM, VALUE_FROM)
                                .param(TO_PARAM, VALUE_TO)
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        UserResponseDto[] actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsByteArray(),
                UserResponseDto[].class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(EXPECTED_LENGTH_FOR_REQUEST_FIND_BY_BIRTHDATE_BETWEEN, actual.length);
        Assertions.assertEquals(expected.getLastName(), actual[RESPONSE_POSITION].getLastName());
        Assertions.assertEquals(expected.getAddress(), actual[RESPONSE_POSITION].getAddress());
        EqualsBuilder.reflectionEquals(expected, actual[RESPONSE_POSITION], ID_FIELD);
    }

    @Test
    @WithMockUser(roles = USER_ROLE)
    @DisplayName("test for getting user when from param bigger than to param. Should throw exception")
    public void searchByDate_findUserByBirthDayBetweenIfFromBiggerThanTo_Not_Ok() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/users/search-by-date-between")
                                .param(FROM_PARAM, WRONG_VALUE_FROM)
                                .param(TO_PARAM, VALUE_TO)
                                .contentType(APPLICATION_JSON)
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
                                .contentType(APPLICATION_JSON)
                                .content(jsonRequest)
                                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserResponseDto actual = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), UserResponseDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(userRequestDto.getLastName(), actual.getLastName());
        Assertions.assertEquals(userRequestDto.getAddress(), actual.getAddress());
        EqualsBuilder.reflectionEquals(userRequestDto, actual, EMAIL_FIELD);
    }

    @Test
    @WithMockUser(roles = USER_ROLE, username = EXISTING_LOGIN)
    @DisplayName("test throwing exception when you update all not your fields")
    public void updateAllInfo_updateAllFieldByNotYourself_Not_Ok() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(userRequestDto);
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/users/{id}", 2)
                                .contentType(APPLICATION_JSON)
                                .content(jsonRequest)
                                .accept(APPLICATION_JSON))
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
        Assertions.assertEquals(expected.getLastName(), actual.getLastName());
        Assertions.assertEquals(expected.getAddress(), actual.getAddress());
        EqualsBuilder.reflectionEquals(expected, actual, FIRST_NAME_FIELD);
    }
}
