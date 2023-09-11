package au.org.raid.api.validator;

import au.org.raid.api.endpoint.message.ValidationMessage;
import au.org.raid.idl.raidv2.model.Dates;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DatesValidator {
    public List<ValidationFailure> validate(final Dates date) {
        final var failures = new ArrayList<ValidationFailure>();
        if (date == null) {
            failures.add(ValidationMessage.DATES_NOT_SET);
        } else {
            if (date.getStartDate() == null) {
                failures.add(ValidationMessage.DATES_START_DATE_NOT_SET);
            }
        }
        return failures;
    }
}
