package backend.user.restful.app.repository.user.specification;

import static backend.user.restful.app.lib.Constant.ADDRESS_FIELD;
import static backend.user.restful.app.lib.Constant.ROLES_TABLE;

import backend.user.restful.app.model.User;
import backend.user.restful.app.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AddressSpecificationProvider implements SpecificationProvider<User> {
    @Override
    public Specification<User> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            root.fetch(ROLES_TABLE);
            return root.get(ADDRESS_FIELD).in(Arrays.stream(params).toArray());
        };
    }

    @Override
    public String getKey() {
        return ADDRESS_FIELD;
    }
}
