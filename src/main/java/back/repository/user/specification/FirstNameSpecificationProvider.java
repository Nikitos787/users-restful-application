package back.repository.user.specification;

import back.model.User;
import back.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class FirstNameSpecificationProvider implements SpecificationProvider<User> {
    private static final String ROLES_TABLE = "roles";
    private static final String FIRST_NAME_KEY_FIELD = "firstName";

    @Override
    public Specification<User> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            root.fetch(ROLES_TABLE);
            return root.get(FIRST_NAME_KEY_FIELD).in(Arrays.stream(params).toArray());
        };
    }

    @Override
    public String getKey() {
        return FIRST_NAME_KEY_FIELD;
    }
}
