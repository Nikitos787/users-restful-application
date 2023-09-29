package back.lib;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {
    public static final String PHONE_NUMBER_PATTERN = "[0-9]{9}$";
    private static final String FIRST_SYMBOLS = "^";

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
        String exampleForPattern = FIRST_SYMBOLS + prefixPhone + PHONE_NUMBER_PATTERN;
        Pattern pattern = Pattern.compile(exampleForPattern);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}

