package back.lib;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;
import org.springframework.beans.factory.annotation.Value;

public class AgeValidator implements ConstraintValidator<ValidAge, LocalDate> {
    @Value("${age.for.registration}")
    private int requireAge;

    @Override
    public void initialize(ValidAge constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);

    }

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) {
            return false;
        }

        LocalDate now = LocalDate.now();
        Period age = Period.between(birthDate, now);

        return age.getYears() >= requireAge;
    }
}
