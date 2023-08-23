package au.org.raid.api.service.raid.validation;

import au.org.raid.api.repository.RelatedObjectTypeRepository;
import au.org.raid.api.service.doi.DoiService;
import au.org.raid.idl.raidv2.model.RelatedObject;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.service.doi.DoiService.DOI_REGEX;
import static au.org.raid.api.util.StringUtil.isBlank;

@Service
public class StableRelatedObjectValidationService {
    private static final String RELATED_OBJECT_SCHEME_URI = "https://doi.org/";
    private static final String RELATED_OBJECT_TYPE_SCHEME_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/related-object-type/";

    private static final String RELATED_OBJECT_TYPE_URL_PREFIX =
            "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/";

    private static final List<String> VALID_CATEGORY_TYPES =
            List.of("Input", "Output", "Internal process document or artefact");

    private final DoiService doiService;
    private final StableRelatedObjectTypeValidationService typeValidationService;
    private final StableRelatedObjectCategoryValidationService categoryValidationService;

    public StableRelatedObjectValidationService(final RelatedObjectTypeRepository relatedObjectTypeRepository, final DoiService doiService, final StableRelatedObjectTypeValidationService typeValidationService, final StableRelatedObjectCategoryValidationService categoryValidationService) {
        this.doiService = doiService;
        this.typeValidationService = typeValidationService;
        this.categoryValidationService = categoryValidationService;
    }

    public List<ValidationFailure> validateRelatedObjects(final List<RelatedObject> relatedObjects) {
        final var failures = new ArrayList<ValidationFailure>();

        if (relatedObjects == null) {
            return failures;
        }

        IntStream.range(0, relatedObjects.size())
                .forEach(index -> {
                    final var relatedObject = relatedObjects.get(index);

                    final var relatedObjectTypePattern =
                            String.format("^%s[a-z\\-]+\\.json$", RELATED_OBJECT_TYPE_URL_PREFIX);

                    if (isBlank(relatedObject.getId())) {
                        failures.add(new ValidationFailure()
                                .fieldId(String.format("relatedObjects[%d].id", index))
                                .errorType(NOT_SET_TYPE)
                                .message(FIELD_MUST_BE_SET_MESSAGE));
                    } else if (!DOI_REGEX.matcher(relatedObject.getId()).matches()) {
                        failures.add(new ValidationFailure()
                                .fieldId(String.format("relatedObjects[%d].id", index))
                                .errorType("invalid")
                                .message("The related object does not match the expected DOI pattern."));
                    } else {
                        failures.addAll(doiService.validateDoiExists(relatedObject.getId()).stream().map(message ->
                                new ValidationFailure()
                                        .fieldId(String.format("relatedObjects[%d].id", index))
                                        .errorType(INVALID_VALUE_TYPE)
                                        .message(message)
                        ).toList());

                    }

                    if (isBlank(relatedObject.getIdentifierSchemeUri())) {
                        failures.add(new ValidationFailure()
                                .fieldId(String.format("relatedObjects[%d].identifierSchemeUri", index))
                                .errorType(NOT_SET_TYPE)
                                .message(FIELD_MUST_BE_SET_MESSAGE));
                    } else if (!relatedObject.getIdentifierSchemeUri().equals(RELATED_OBJECT_SCHEME_URI)) {
                        failures.add(new ValidationFailure()
                                .fieldId(String.format("relatedObjects[%d].identifierSchemeUri", index))
                                .errorType("invalid")
                                .message(String.format("Only %s is supported.", RELATED_OBJECT_SCHEME_URI)));
                    }

                    failures.addAll(typeValidationService.validate(relatedObject.getType(), index));
                    failures.addAll(categoryValidationService.validate(relatedObject.getCategory(), index));
                });

        return failures;
    }
}
