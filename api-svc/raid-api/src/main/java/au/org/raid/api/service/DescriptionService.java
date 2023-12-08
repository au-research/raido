package au.org.raid.api.service;

import au.org.raid.api.factory.record.RaidDescriptionRecordFactory;
import au.org.raid.api.repository.DescriptionTypeRepository;
import au.org.raid.api.repository.DescriptionTypeSchemaRepository;
import au.org.raid.api.repository.RaidDescriptionRepository;
import au.org.raid.idl.raidv2.model.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DescriptionService {
    private final DescriptionTypeSchemaRepository descriptionTypeSchemaRepository;
    private final DescriptionTypeRepository descriptionTypeRepository;
    private final LanguageService languageService;
    private final RaidDescriptionRecordFactory raidDescriptionRecordFactory;
    private final RaidDescriptionRepository raidDescriptionRepository;

    public void create(final List<Description> descriptions, final String raidName) {
        for (final var description : descriptions) {
            final var descriptionType = description.getType();

            final var descriptionTypeSchemaRecord = descriptionTypeSchemaRepository
                    .findByUri(descriptionType.getSchemaUri())
                    .orElseThrow(() -> new RuntimeException(
                            "Description type schema not found %s".formatted(descriptionType.getSchemaUri())));

            final var descriptionTypeRecord = descriptionTypeRepository
                    .findByUriAndSchemeId(descriptionType.getId(), descriptionTypeSchemaRecord.getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Description type %s not found in schema %s".formatted(descriptionType.getId(), descriptionType.getSchemaUri())));

            final var languageId = languageService.findLanguageId(description.getLanguage());

            final var raidDescriptionRecord = raidDescriptionRecordFactory.create(
                    raidName,
                    description.getText(),
                    descriptionTypeRecord.getId(),
                    languageId);

            raidDescriptionRepository.create(raidDescriptionRecord);
        }
    }
}
