package raido.apisvc.service.raid.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import raido.apisvc.repository.RelatedRaidTypeRepository;
import raido.apisvc.repository.RelatedRaidTypeSchemeRepository;
import raido.idl.raidv2.model.RelatedRaidType;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;

import static raido.apisvc.endpoint.message.ValidationMessage.*;
import static raido.apisvc.util.StringUtil.isBlank;

@Component
@RequiredArgsConstructor
public class StableRelatedRaidTypeValidationService {
    private final RelatedRaidTypeSchemeRepository relatedRaidTypeSchemeRepository;
    private final RelatedRaidTypeRepository relatedRaidTypeRepository;

    public List<ValidationFailure> validate(final RelatedRaidType relatedRaidType, final int index) {
        final var failures = new ArrayList<ValidationFailure>();

        if (relatedRaidType == null) {
            return List.of(new ValidationFailure()
                .fieldId("relatedRaids[%d].type".formatted(index))
                .errorType(NOT_SET_TYPE)
                .message(FIELD_MUST_BE_SET_MESSAGE)
            );
        }

        if (isBlank(relatedRaidType.getId())) {
            failures.add(new ValidationFailure()
                .fieldId("relatedRaids[%d].type.id".formatted(index))
                .errorType(NOT_SET_TYPE)
                .message(FIELD_MUST_BE_SET_MESSAGE)
            );
        }

        if (isBlank(relatedRaidType.getSchemeUri())) {
            failures.add(new ValidationFailure()
                .fieldId("relatedRaids[%d].type.schemeUri".formatted(index))
                .errorType(NOT_SET_TYPE)
                .message(FIELD_MUST_BE_SET_MESSAGE)
            );
        } else {
            final var relatedRaidTypeScheme =
                relatedRaidTypeSchemeRepository.findByUri(relatedRaidType.getSchemeUri());

            if (relatedRaidTypeScheme.isEmpty()) {
                failures.add(new ValidationFailure()
                    .fieldId("relatedRaids[%d].type.schemeUri".formatted(index))
                    .errorType(INVALID_VALUE_TYPE)
                    .message(INVALID_SCHEME));
            } else if (!isBlank(relatedRaidType.getId()) &&
                relatedRaidTypeRepository.findByUriAndSchemeId(relatedRaidType.getId(), relatedRaidTypeScheme.get().getId()).isEmpty()) {
                failures.add(new ValidationFailure()
                    .fieldId("relatedRaids[%d].type.id".formatted(index))
                    .errorType(INVALID_VALUE_TYPE)
                    .message(INVALID_ID_FOR_SCHEME));
            }
        }

        return failures;
    }

}