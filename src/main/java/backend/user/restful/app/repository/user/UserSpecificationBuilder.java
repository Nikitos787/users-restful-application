package backend.user.restful.app.repository.user;

import static backend.user.restful.app.lib.Constant.ADDRESS_FIELD;
import static backend.user.restful.app.lib.Constant.BIRTHDATE_FIELD;
import static backend.user.restful.app.lib.Constant.EMAIL_FIELD;
import static backend.user.restful.app.lib.Constant.FIRST_NAME_FIELD;
import static backend.user.restful.app.lib.Constant.LAST_NAME_FIELD;
import static backend.user.restful.app.lib.Constant.PHONE_NUMBER_FIELD;

import backend.user.restful.app.dto.UserSearchParametersDto;
import backend.user.restful.app.model.User;
import backend.user.restful.app.repository.SpecificationBuilder;
import backend.user.restful.app.repository.SpecificationProviderManager;
import java.time.LocalDate;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSpecificationBuilder implements SpecificationBuilder<User> {
    private final SpecificationProviderManager<User> specificationProviderManager;

    @Override
    public Specification<User> build(UserSearchParametersDto searchParametersDto) {
        Specification<User> specification = Specification.where(null);
        if (searchParametersDto.getFirstNames() != null
                && searchParametersDto.getFirstNames().length > 0) {
            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider(FIRST_NAME_FIELD)
                    .getSpecification(searchParametersDto.getFirstNames()));
        }
        if (searchParametersDto.getLastNames() != null
                && searchParametersDto.getLastNames().length > 0) {
            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider(LAST_NAME_FIELD)
                    .getSpecification(searchParametersDto.getLastNames()));
        }
        if (searchParametersDto.getAddresses() != null
                && searchParametersDto.getAddresses().length > 0) {
            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider(ADDRESS_FIELD)
                    .getSpecification(searchParametersDto.getAddresses()));
        }
        if (searchParametersDto.getEmails() != null
                && searchParametersDto.getEmails().length > 0) {
            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider(EMAIL_FIELD)
                    .getSpecification(searchParametersDto.getEmails()));
        }
        if (searchParametersDto.getBirthdates() != null
                && searchParametersDto.getBirthdates().length > 0) {
            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider(BIRTHDATE_FIELD)
                    .getSpecification(
                            Arrays.stream(searchParametersDto.getBirthdates())
                                    .map(LocalDate::toString)
                                    .toArray(String[]::new))
            );
        }
        if (searchParametersDto.getPhoneNumbers() != null
                && searchParametersDto.getPhoneNumbers().length > 0) {
            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider(PHONE_NUMBER_FIELD)
                    .getSpecification(searchParametersDto.getPhoneNumbers()));
        }
        return specification;
    }
}
