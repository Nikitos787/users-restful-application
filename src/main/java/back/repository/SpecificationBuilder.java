package back.repository;

import back.dto.UserSearchParametersDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(UserSearchParametersDto userSearchParametersDto);
}
