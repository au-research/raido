package au.org.raid.api.service.raid.validation;

import au.org.raid.api.service.raid.ValidationFailureException;
import au.org.raid.api.service.raid.id.IdentifierHandle;
import au.org.raid.api.service.raid.id.IdentifierParser;
import au.org.raid.api.service.raid.id.IdentifierUrl;
import au.org.raid.api.util.Log;
import au.org.raid.idl.raidv2.model.IdBlock;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static au.org.raid.api.endpoint.message.ValidationMessage.HANDLE_DOES_NOT_MATCH_MESSAGE;
import static au.org.raid.api.endpoint.message.ValidationMessage.handlesDoNotMatch;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.StringUtil.areDifferent;

@Component
public class IdBlockValidationService {
    private static final Log log = to(IdBlockValidationService.class);

    private final IdentifierParser idParser;

    public IdBlockValidationService(final IdentifierParser idParser) {
        this.idParser = idParser;
    }

    public List<ValidationFailure> validateUpdateHandle(final String decodedHandleFromPath, final IdBlock updateIdBlock) {
        final var failures = new ArrayList<ValidationFailure>();

        IdentifierUrl updateId = null;
        try {
            updateId = idParser.parseUrlWithException(updateIdBlock.getIdentifier());
        } catch (ValidationFailureException e) {
            failures.addAll(e.getFailures());
        }

        IdentifierHandle pathHandle = null;
        try {
            pathHandle = idParser.parseHandleWithException(decodedHandleFromPath);
        } catch (ValidationFailureException e) {
            failures.addAll(e.getFailures());
        }

        if (updateId != null && pathHandle != null) {
            if (areDifferent(pathHandle.format(), updateId.handle().format())) {
                log.with("pathHandle", pathHandle.format()).
                        with("updateId", updateId.handle().format()).
                        error(HANDLE_DOES_NOT_MATCH_MESSAGE);
                failures.add(handlesDoNotMatch());
            }
        }

        return failures;
    }
}
