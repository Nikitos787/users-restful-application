package back.lib;

import back.dto.BirthDateBetweenSearchParametersDto;
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
