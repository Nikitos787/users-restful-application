package backend.user.restful.app.lib;

import backend.user.restful.app.dto.BirthDateBetweenSearchParametersDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange,
        BirthDateBetweenSearchParametersDto> {

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BirthDateBetweenSearchParametersDto birthDateBetweenSearchParametersDto,
                           ConstraintValidatorContext constraintValidatorContext) {
        return birthDateBetweenSearchParametersDto.getFrom()
                .isBefore(birthDateBetweenSearchParametersDto.getTo());
    }
}
