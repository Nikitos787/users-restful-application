package backend.user.restful.app.lib;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {
    @Value("${prefix.for.phone.number}")
    private String prefixPhone;

    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String phoneNumber,
                           ConstraintValidatorContext constraintValidatorContext) {

        if (phoneNumber == null) {
            return false;
        }
        String exampleForPattern = Constant.FIRST_SYMBOLS + prefixPhone
                + Constant.PHONE_NUMBER_PATTERN;
        Pattern pattern = Pattern.compile(exampleForPattern);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}

