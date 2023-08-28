package au.org.raid.api.validator;

import au.org.raid.api.endpoint.message.ValidationMessage;
import au.org.raid.api.service.raid.validation.AccessStatementValidationService;
import au.org.raid.idl.raidv2.model.Access;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static au.org.raid.api.endpoint.message.ValidationMessage.NOT_SET_MESSAGE;
import static au.org.raid.api.endpoint.message.ValidationMessage.NOT_SET_TYPE;
import static au.org.raid.api.util.StringUtil.isBlank;

@Component
@RequiredArgsConstructor
public class AccessValidator {
    private static final String ACCESS_TYPE_EMBARGOED =
            "https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/embargoed.json";
    private static final String ACCESS_TYPE_OPEN =
            "https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/open.json";
    private final AccessTypeValidator typeValidationService;
    private final AccessStatementValidationService accessStatementValidationService;

    public List<ValidationFailure> validate(
            Access access
    ) {
        var failures = new ArrayList<ValidationFailure>();

        if (access == null) {
            failures.add(ValidationMessage.ACCESS_NOT_SET);
        } else if (access.getType() == null) {
            failures.add(
                    new ValidationFailure()
                            .errorType(NOT_SET_TYPE)
                            .fieldId("access.type")
                            .message(NOT_SET_MESSAGE)
            );
        } else {
            failures.addAll(typeValidationService.validate(access.getType()));

            if (!isBlank(access.getType().getId())) {
                final var typeId = access.getType().getId();

                if (!typeId.equals(ACCESS_TYPE_OPEN)) { // TODO: needs to be validated regardless of access type
                    failures.addAll(
                            accessStatementValidationService.validate(access.getAccessStatement())
                    );

                }
                if (typeId.equals(ACCESS_TYPE_EMBARGOED) && access.getEmbargoExpiry() == null) {
                    failures.add(new ValidationFailure()
                            .errorType(NOT_SET_TYPE)
                            .fieldId("access.embargoExpiry")
                            .message(NOT_SET_MESSAGE));
                }
            }
        }


        return failures;
    }
}