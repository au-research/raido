package au.org.raid.api.validator;

import au.org.raid.idl.raidv2.model.RelatedRaid;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static au.org.raid.api.endpoint.message.ValidationMessage.NOT_SET_MESSAGE;
import static au.org.raid.api.endpoint.message.ValidationMessage.NOT_SET_TYPE;
import static au.org.raid.api.util.StringUtil.isBlank;

@Service
@RequiredArgsConstructor
public class RelatedRaidValidator {

    private final RelatedRaidTypeValidator typeValidationService;

    public List<ValidationFailure> validate(final List<RelatedRaid> relatedRaids) {
        final var failures = new ArrayList<ValidationFailure>();

        if (relatedRaids == null) {
            return failures;
        }

        IntStream.range(0, relatedRaids.size())
                .forEach(index -> {
                    final var relatedRaid = relatedRaids.get(index);

                    if (isBlank(relatedRaid.getId())) {
                        failures.add(new ValidationFailure()
                                .fieldId(String.format("relatedRaid[%d].id", index))
                                .errorType(NOT_SET_TYPE)
                                .message(NOT_SET_MESSAGE));
                    }
                    // TODO: Validate Raid exists

                    failures.addAll(typeValidationService.validate(relatedRaid.getType(), index));
                });

        return failures;
    }
}