package au.org.raid.api.service.raid.validation;

import au.org.raid.api.endpoint.message.ValidationMessage;
import au.org.raid.idl.raidv2.model.AccessBlock;
import au.org.raid.idl.raidv2.model.AccessType;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AccessBlockValidationService {

    public List<ValidationFailure> validateAccess(
            AccessBlock access
    ) {
        var failures = new ArrayList<ValidationFailure>();

        if (access == null) {
            failures.add(ValidationMessage.ACCESS_NOT_SET);
        } else {
            if (access.getType() == null) {
                failures.add(ValidationMessage.ACCESS_TYPE_NOT_SET);
            } else {
                if (
                        access.getType() == AccessType.CLOSED &&
                                access.getAccessStatement() == null
                ) {
                    failures.add(ValidationMessage.ACCESS_STATEMENT_NOT_SET);
                }
            }
        }

        return failures;
    }
}