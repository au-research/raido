package au.org.raid.api.validator;

import au.org.raid.api.repository.RelatedObjectTypeRepository;
import au.org.raid.api.repository.RelatedObjectTypeSchemeRepository;
import au.org.raid.idl.raidv2.model.RelatedObjectType;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.util.StringUtil.isBlank;

@Component
public class RelatedObjectTypeValidator {
    private final RelatedObjectTypeRepository relatedObjectTypeRepository;
    private final RelatedObjectTypeSchemeRepository relatedObjectTypeSchemeRepository;

    public RelatedObjectTypeValidator(final RelatedObjectTypeRepository relatedObjectTypeRepository, final RelatedObjectTypeSchemeRepository relatedObjectTypeSchemeRepository) {
        this.relatedObjectTypeRepository = relatedObjectTypeRepository;
        this.relatedObjectTypeSchemeRepository = relatedObjectTypeSchemeRepository;
    }


    public List<ValidationFailure> validate(final RelatedObjectType relatedObjectType, final int index) {
        var failures = new ArrayList<ValidationFailure>();

        if (isBlank(relatedObjectType.getId())) {
            failures.add(new ValidationFailure()
                    .fieldId("relatedObjects[%d].type.id".formatted(index))
                    .errorType(NOT_SET_TYPE)
                    .message(FIELD_MUST_BE_SET_MESSAGE)
            );
        }
        if (isBlank(relatedObjectType.getSchemeUri())) {
            failures.add(new ValidationFailure()
                    .fieldId("relatedObjects[%d].type.schemeUri".formatted(index))
                    .errorType(NOT_SET_TYPE)
                    .message(FIELD_MUST_BE_SET_MESSAGE)
            );
        } else {
            final var relatedObjectTypeScheme =
                    relatedObjectTypeSchemeRepository.findByUri(relatedObjectType.getSchemeUri());

            if (relatedObjectTypeScheme.isEmpty()) {
                failures.add(new ValidationFailure()
                        .fieldId("relatedObjects[%d].type.schemeUri".formatted(index))
                        .errorType(INVALID_VALUE_TYPE)
                        .message(INVALID_SCHEME)
                );
            } else if (!isBlank(relatedObjectType.getId()) &&
                    relatedObjectTypeRepository.findByUriAndSchemeId(relatedObjectType.getId(), relatedObjectTypeScheme.get().getId()).isEmpty()) {
                failures.add(new ValidationFailure()
                        .fieldId("relatedObjects[%d].type.id".formatted(index))
                        .errorType(INVALID_VALUE_TYPE)
                        .message(INVALID_ID_FOR_SCHEME)
                );
            }
        }

        return failures;
    }
}
