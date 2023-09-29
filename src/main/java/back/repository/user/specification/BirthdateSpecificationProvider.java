package back.repository.user.specification;

import back.model.User;
import back.repository.SpecificationProvider;
import java.time.LocalDate;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class BirthdateSpecificationProvider implements SpecificationProvider<User> {
    private static final String ROLES_TABLE = "roles";
    private static final String BIRTHDATE_KEY_FIELD = "birthdate";

    @Override
    public Specification<User> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            root.fetch(ROLES_TABLE);
            return root.get(BIRTHDATE_KEY_FIELD).in(Arrays.stream(params)
                    .map(LocalDate::parse)
                    .toArray());
        };
    }

    @Override
    public String getKey() {
        return BIRTHDATE_KEY_FIELD;
    }
}
