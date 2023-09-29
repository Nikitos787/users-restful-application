package backend.user.restful.app.repository.user.specification;

import backend.user.restful.app.lib.Constant;
import backend.user.restful.app.model.User;
import backend.user.restful.app.repository.SpecificationProvider;
import java.time.LocalDate;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class BirthdateSpecificationProvider implements SpecificationProvider<User> {
    @Override
    public Specification<User> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            root.fetch(Constant.ROLES_TABLE);
            return root.get(Constant.BIRTHDATE_FIELD).in(Arrays.stream(params)
                    .map(LocalDate::parse)
                    .toArray());
        };
    }

    @Override
    public String getKey() {
        return Constant.BIRTHDATE_FIELD;
    }
}
