package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Service;
import raido.apisvc.repository.RelatedObjectTypeRepository;
import raido.apisvc.service.doi.DoiService;
import raido.idl.raidv2.model.RelatedObjectBlock;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static raido.apisvc.endpoint.message.ValidationMessage.INVALID_VALUE_TYPE;
import static raido.apisvc.service.doi.DoiService.DOI_REGEX;

@Service
public class RelatedObjectValidationService {
  private static final String RELATED_OBJECT_SCHEME_URI = "https://doi.org/";
  private static final String RELATED_OBJECT_TYPE_SCHEME_URI =
    "https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/related-object-type/";

  private static final String RELATED_OBJECT_TYPE_URL_PREFIX =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/";

  private static final List<String> VALID_CATEGORY_TYPES =
    List.of("Input", "Output", "Internal process document or artefact");
  private final RelatedObjectTypeRepository relatedObjectTypeRepository;

  private final DoiService doiService;

  public RelatedObjectValidationService(final RelatedObjectTypeRepository relatedObjectTypeRepository, final DoiService doiService) {
    this.relatedObjectTypeRepository = relatedObjectTypeRepository;
    this.doiService = doiService;
  }

  public List<ValidationFailure> validateRelatedObjects(final List<RelatedObjectBlock> relatedObjects) {
    final var failures = new ArrayList<ValidationFailure>();

    if (relatedObjects == null) {
      return failures;
    }

    IntStream.range(0, relatedObjects.size())
      .forEach(i -> {
        final var relatedObject = relatedObjects.get(i);

        final var relatedObjectTypePattern =
          String.format("^%s[a-z\\-]+\\.json$", RELATED_OBJECT_TYPE_URL_PREFIX);

        if (relatedObject.getRelatedObject() == null) {
          failures.add(new ValidationFailure()
            .fieldId(String.format("relatedObjects[%d].relatedObject", i))
            .errorType("required")
            .message("This is a required field."));
        }
        else if (!DOI_REGEX.matcher(relatedObject.getRelatedObject()).matches()) {
          failures.add(new ValidationFailure()
            .fieldId(String.format("relatedObjects[%d].relatedObject", i))
            .errorType("invalid")
            .message("The related object does not match the expected DOI pattern."));
        }
        else {
          failures.addAll(doiService.validateDoiExists(relatedObject.getRelatedObject()).stream().map(message ->
            new ValidationFailure()
              .fieldId(String.format("relatedObjects[%d].id", i))
              .errorType(INVALID_VALUE_TYPE)
              .message(message)
          ).toList());

        }

        if (relatedObject.getRelatedObjectSchemeUri() == null) {
          failures.add(new ValidationFailure()
            .fieldId(String.format("relatedObjects[%d].relatedObjectSchemeUri", i))
            .errorType("required")
            .message("This is a required field."));
        }
        else if (!relatedObject.getRelatedObjectSchemeUri().equals(RELATED_OBJECT_SCHEME_URI)) {
          failures.add(new ValidationFailure()
            .fieldId(String.format("relatedObjects[%d].relatedObjectSchemeUri", i))
            .errorType("invalid")
            .message(String.format("Only %s is supported.", RELATED_OBJECT_SCHEME_URI)));
        }

        if (relatedObject.getRelatedObjectType() == null) {
          failures.add(new ValidationFailure()
            .fieldId(String.format("relatedObjects[%d].relatedObjectType", i))
            .errorType("required")
            .message("This is a required field."));
        }
        else if (!relatedObject.getRelatedObjectType().matches(relatedObjectTypePattern)) {
          failures.add(new ValidationFailure()
            .fieldId(String.format("relatedObjects[%d].relatedObjectType", i))
            .errorType("invalid")
            .message("Related object type is invalid."));
        }
//        else if (relatedObjectTypeRepository.findByUriAnd(relatedObject.getRelatedObjectType()).isEmpty()) {
//          failures.add(new ValidationFailure()
//            .fieldId(String.format("relatedObjects[%d].relatedObjectType", i))
//            .errorType("invalid")
//            .message("Related object type was not found."));
//        }

        if (relatedObject.getRelatedObjectTypeSchemeUri() == null) {
          failures.add(new ValidationFailure()
            .fieldId(String.format("relatedObjects[%d].relatedObjectTypeSchemeUri", i))
            .errorType("required")
            .message("This is a required field."));

        }
        else if (!relatedObject.getRelatedObjectTypeSchemeUri().equals(RELATED_OBJECT_TYPE_SCHEME_URI)) {
          failures.add(new ValidationFailure()
            .fieldId(String.format("relatedObjects[%d].relatedObjectTypeSchemeUri", i))
            .errorType("invalid")
            .message(String.format("Only %s is supported.", RELATED_OBJECT_TYPE_SCHEME_URI)));
        }

        if (relatedObject.getRelatedObjectCategory() == null) {
          failures.add(new ValidationFailure()
            .fieldId(String.format("relatedObjects[%d].relatedObjectCategory", i))
            .errorType("required")
            .message("This is a required field."));
        }
        else if (!VALID_CATEGORY_TYPES.contains(relatedObject.getRelatedObjectCategory())) {
          failures.add(new ValidationFailure()
            .fieldId(String.format("relatedObjects[%d].relatedObjectCategory", i))
            .errorType("invalid")
            .message(String.format("Should be one of %s.", "'" + String.join("', '", VALID_CATEGORY_TYPES) + "'")));
        }
      });

    return failures;
  }
}
