package au.org.raid.api.service.raid.validation;

import au.org.raid.api.repository.RelatedObjectCategoryRepository;
import au.org.raid.api.repository.RelatedObjectCategorySchemeRepository;
import au.org.raid.idl.raidv2.model.RelatedObjectCategory;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.util.StringUtil.isBlank;

@Component
public class StableRelatedObjectCategoryValidationService {
    private final RelatedObjectCategoryRepository relatedObjectCategoryRepository;
    private final RelatedObjectCategorySchemeRepository relatedObjectCategorySchemeRepository;

    public StableRelatedObjectCategoryValidationService(final RelatedObjectCategoryRepository relatedObjectCategoryRepository, final RelatedObjectCategorySchemeRepository relatedObjectCategorySchemeRepository) {
        this.relatedObjectCategoryRepository = relatedObjectCategoryRepository;
        this.relatedObjectCategorySchemeRepository = relatedObjectCategorySchemeRepository;
    }

    public List<ValidationFailure> validate(final RelatedObjectCategory relatedObjectCategory, final int index) {
        var failures = new ArrayList<ValidationFailure>();

        if (isBlank(relatedObjectCategory.getId())) {
            failures.add(new ValidationFailure()
                    .fieldId("relatedObjects[%d].category.id".formatted(index))
                    .errorType(NOT_SET_TYPE)
                    .message(FIELD_MUST_BE_SET_MESSAGE)
            );
        }
        if (isBlank(relatedObjectCategory.getSchemeUri())) {
            failures.add(new ValidationFailure()
                    .fieldId("relatedObjects[%d].category.schemeUri".formatted(index))
                    .errorType(NOT_SET_TYPE)
                    .message(FIELD_MUST_BE_SET_MESSAGE)
            );
        } else {
            final var relatedObjectCategoryScheme =
                    relatedObjectCategorySchemeRepository.findByUri(relatedObjectCategory.getSchemeUri());

            if (relatedObjectCategoryScheme.isEmpty()) {
                failures.add(new ValidationFailure()
                        .fieldId("relatedObjects[%d].category.schemeUri".formatted(index))
                        .errorType(INVALID_VALUE_TYPE)
                        .message(INVALID_SCHEME)
                );
            } else if (!isBlank(relatedObjectCategory.getId()) &&
                    relatedObjectCategoryRepository.findByUriAndSchemeId(relatedObjectCategory.getId(), relatedObjectCategoryScheme.get().getId()).isEmpty()) {
                failures.add(new ValidationFailure()
                        .fieldId("relatedObjects[%d].category.id".formatted(index))
                        .errorType(INVALID_VALUE_TYPE)
                        .message(INVALID_ID_FOR_SCHEME)
                );
            }
        }

        return failures;
    }
}
