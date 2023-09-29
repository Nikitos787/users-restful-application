package back.repository.user;

import back.dto.UserSearchParametersDto;
import back.model.User;
import back.repository.SpecificationBuilder;
import back.repository.SpecificationProviderManager;
import java.time.LocalDate;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSpecificationBuilder implements SpecificationBuilder<User> {
    private static final String FIRST_NAME_KEY_FIELD = "firstName";
    private static final String LAST_NAME_KEY_FIELD = "lastName";
    private static final String ADDRESS_KEY_FIELD = "address";
    private static final String BIRTHDATE_KEY_FIELD = "birthdate";
    private static final String EMAIL_KEY_FIELD = "email";
    private static final String PHONE_NUMBER_KEY_FIELD = "phoneNumber";

    private final SpecificationProviderManager<User> specificationProviderManager;

    @Override
    public Specification<User> build(UserSearchParametersDto searchParametersDto) {
        Specification<User> specification = Specification.where(null);
        if (searchParametersDto.getFirstNames() != null
                && searchParametersDto.getFirstNames().length > 0) {
            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider(FIRST_NAME_KEY_FIELD)
                    .getSpecification(searchParametersDto.getFirstNames()));
        }
        if (searchParametersDto.getLastNames() != null
                && searchParametersDto.getLastNames().length > 0) {
            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider(LAST_NAME_KEY_FIELD)
                    .getSpecification(searchParametersDto.getLastNames()));
        }
        if (searchParametersDto.getAddresses() != null
                && searchParametersDto.getAddresses().length > 0) {
            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider(ADDRESS_KEY_FIELD)
                    .getSpecification(searchParametersDto.getAddresses()));
        }
        if (searchParametersDto.getEmails() != null
                && searchParametersDto.getEmails().length > 0) {
            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider(EMAIL_KEY_FIELD)
                    .getSpecification(searchParametersDto.getEmails()));
        }
        if (searchParametersDto.getBirthdates() != null
                && searchParametersDto.getBirthdates().length > 0) {
            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider(BIRTHDATE_KEY_FIELD)
                    .getSpecification(
                            Arrays.stream(searchParametersDto.getBirthdates())
                                    .map(LocalDate::toString)
                                    .toArray(String[]::new))
            );
        }
        if (searchParametersDto.getPhoneNumbers() != null
                && searchParametersDto.getPhoneNumbers().length > 0) {
            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider(PHONE_NUMBER_KEY_FIELD)
                    .getSpecification(searchParametersDto.getPhoneNumbers()));
        }
        return specification;
    }
}
