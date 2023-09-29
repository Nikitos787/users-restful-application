package back.dto;

import back.lib.ValidDateRange;
import java.time.LocalDate;
import lombok.Data;

@Data
@ValidDateRange(message = "From date must be less than or equal to To date")
public class BirthDateBetweenSearchParametersDto {
    private LocalDate from;
    private LocalDate to;
}
