package backend.user.restful.app.repository;

import backend.user.restful.app.dto.UserSearchParametersDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(UserSearchParametersDto userSearchParametersDto);
}
