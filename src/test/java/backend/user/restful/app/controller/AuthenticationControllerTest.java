package backend.user.restful.app.controller;

import static backend.user.restful.app.lib.Constant.ADDRESS_FIELD;
import static backend.user.restful.app.lib.Constant.BIRTHDATE_FIELD;
import static backend.user.restful.app.lib.Constant.EMAIL_FIELD;
import static backend.user.restful.app.lib.Constant.EXISTING_LOGIN;
import static backend.user.restful.app.lib.Constant.FIRST_NAME_FIELD;
import static backend.user.restful.app.lib.Constant.ID_FIELD;
import static backend.user.restful.app.lib.Constant.LAST_NAME_FIELD;
import static backend.user.restful.app.lib.Constant.PHONE_NUMBER_FIELD;
import static backend.user.restful.app.lib.Constant.WORNG_PASSWORD;
import static backend.user.restful.app.lib.Constant.getUserRegistrationRequestDto;
import static backend.user.restful.app.lib.Constant.getUserResponseDto;
import static backend.user.restful.app.lib.Constant.getuserLoginRequestDto;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import backend.user.restful.app.dto.UserLoginRequestDto;
import backend.user.restful.app.dto.UserRegistrationRequestDto;
import backend.user.restful.app.dto.UserResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {
    protected static MockMvc mockMvc;

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

    @BeforeEach
    void setUp() {
        userRegistrationRequestDto = getUserRegistrationRequestDto();
        expected = getUserResponseDto();
        userLoginRequestDto = getuserLoginRequestDto();
    }

    @Test
    @DisplayName("test for successful registration")
    public void register_SuccessfulCase_ok() throws Exception {
        userRegistrationRequestDto.setEmail("newuser@gmail.com");
        String jsonRequest = objectMapper.writeValueAsString(userRegistrationRequestDto);
        MvcResult result = mockMvc.perform(
                        post("/api/auth/registration")
                                .contentType(APPLICATION_JSON)
                                .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), UserResponseDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, ID_FIELD);
    }

    @Test
    @DisplayName("test for unsuccessful registration")
    public void register_UnSuccessfulCase_notOk() throws Exception {
        userRegistrationRequestDto.setEmail(EXISTING_LOGIN);
        String jsonRequest = objectMapper.writeValueAsString(userRegistrationRequestDto);
        mockMvc.perform(
                        post("/api/auth/registration")
                                .contentType(APPLICATION_JSON)
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
                                .contentType(APPLICATION_JSON)
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
                                .contentType(APPLICATION_JSON)
                                .content(jsonRequest))
                .andExpect(status().is4xxClientError())
                .andReturn();
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
}
