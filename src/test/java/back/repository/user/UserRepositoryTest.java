package back.repository.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import javax.sql.DataSource;
import back.model.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    private static final String FIRST_DATE = "1980-01-01";
    private static final String SECOND_DATE = "1989-01-01";
    private static final Integer EXPECTED_LENGTH = 2;
    private static final Long EXPECTED_LENGTH_FOR_ALL_USER_IN_TEST_DB = 3L;
    private static final Long ID = 1L;
    private static final Long WRONG_ID = 1000L;
    private static final Long ID_FOR_USER_BY_EMAIL = 3L;
    private static final String EMAIL_FOR_SEARCH = "user3@gmail.com";
    private static final String WRONG_EMAIL_FOR_SEARCH = "someemail.com";
    private static final Integer PAGE_NUMBER = 0;
    private static final Integer PAGE_SIZE = 10;
    @Autowired
    private UserRepository userRepository;

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
    @DisplayName("Test for getting user with birthdate between dates")
    void findAllByBirthdateBetween_ShouldReturnUserWithBirthdateBetweenTwoDates_Ok() {
        LocalDate firstLocalDate = LocalDate.parse(FIRST_DATE);
        LocalDate secondLocalDate = LocalDate.parse(SECOND_DATE);

        List<User> actual = userRepository.findAllByBirthdateBetween(firstLocalDate, secondLocalDate);
        assertEquals(EXPECTED_LENGTH, actual.size());
    }

    @Test
    @DisplayName("Test for getting user by id")
    void findById_ShouldReturnUserById_Ok() {
        User actual = userRepository.findById(ID).orElseThrow();
        assertEquals(ID, actual.getId());
    }

    @Test
    @DisplayName("Test for trowing exception when getting user by id")
    void findById_ReturnUserById_Not_Ok() {
        assertThrows(NoSuchElementException.class, () -> {
            userRepository.findById(WRONG_ID).get();
        }, "NoSuchElementException expected");
    }

    @Test
    @DisplayName("Test for getting user by id")
    void findByEmail_ShouldReturnUserByEmail_Ok() {
        User actual = userRepository.findByEmail(EMAIL_FOR_SEARCH).orElseThrow();
        assertEquals(ID_FOR_USER_BY_EMAIL, actual.getId());
        assertEquals(EMAIL_FOR_SEARCH, actual.getEmail());
    }

    @Test
    @DisplayName("Test for throwing exception when try to get user by email")
    void findByEmail_ReturnUserByEmail_NotOk() {
        assertThrows(NoSuchElementException.class, () -> {
            userRepository.findByEmail(WRONG_EMAIL_FOR_SEARCH).get();
        }, "NoSuchElementException expected");
    }

    @Test
    @DisplayName("Test for getting user by id")
    void findAll_ShouldReturnAllUsers_Ok() {
        Page<User> actual = userRepository.findAll(PageRequest.of(PAGE_NUMBER, PAGE_SIZE));
        assertEquals(EXPECTED_LENGTH_FOR_ALL_USER_IN_TEST_DB, actual.getTotalElements());
    }
}
