package back.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class UserSearchParametersDto {
    private String[] firstNames;
    private String[] lastNames;
    private String[] emails;
    private String[] addresses;
    private String[] phoneNumbers;
    private LocalDate[] birthdates;
}
