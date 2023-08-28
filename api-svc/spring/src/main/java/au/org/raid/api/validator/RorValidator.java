package au.org.raid.api.validator;

import au.org.raid.api.service.ror.RorService;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static au.org.raid.api.endpoint.message.ValidationMessage.NOT_SET_MESSAGE;
import static au.org.raid.api.endpoint.message.ValidationMessage.NOT_SET_TYPE;
import static au.org.raid.api.util.StringUtil.isBlank;

@Component
public class RorValidator {
    private final RorService rorService;

    public RorValidator(final RorService rorService) {
        this.rorService = rorService;
    }

    public List<ValidationFailure> validate(final String ror, final int index) {
        final var failures = new ArrayList<ValidationFailure>();

        if (isBlank(ror)) {
            failures.add(new ValidationFailure()
                    .fieldId("organisations[%d].id".formatted(index))
                    .errorType(NOT_SET_TYPE)
                    .message(NOT_SET_MESSAGE));
        } else {
            failures.addAll(rorService.validate(ror, "organisations[%d].id".formatted(index)));
        }

        return failures;
    }
}
