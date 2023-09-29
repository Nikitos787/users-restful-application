package backend.user.restful.app.repository.user;

import backend.user.restful.app.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @Query("FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @NonNull
    @Query("FROM User u LEFT JOIN FETCH u.roles")
    Page<User> findAll(@NonNull Pageable pageable);

    @NonNull
    @Query("FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findById(@NonNull @Param("id") Long id);

    @Query("FROM User u LEFT JOIN FETCH u.roles WHERE u.birthdate BETWEEN ?1 AND ?2")
    List<User> findAllByBirthdateBetween(LocalDate from, LocalDate to);
}
