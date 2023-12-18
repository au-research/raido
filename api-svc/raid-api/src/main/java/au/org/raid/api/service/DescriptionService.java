package au.org.raid.api.service;

import au.org.raid.api.factory.DescriptionFactory;
import au.org.raid.api.factory.record.RaidDescriptionRecordFactory;
import au.org.raid.api.repository.DescriptionTypeRepository;
import au.org.raid.api.repository.DescriptionTypeSchemaRepository;
import au.org.raid.api.repository.RaidDescriptionRepository;
import au.org.raid.idl.raidv2.model.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final DescriptionFactory descriptionFactory;

    public void create(final List<Description> descriptions, final String handle) {
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
                    handle,
                    description.getText(),
                    descriptionTypeRecord.getId(),
                    languageId);

            raidDescriptionRepository.create(raidDescriptionRecord);
        }
    }

    public List<Description> findAllByHandle(final String handle) {
        final var descriptions = new ArrayList<Description>();
        final var records = raidDescriptionRepository.findAllByHandle(handle);

        for (final var record : records) {
            final var typeId = record.getDescriptionTypeId();

            final var typeRecord = descriptionTypeRepository.findById(typeId)
                    .orElseThrow(() -> new RuntimeException("Description type id not found: %d".formatted(typeId)));

            final var typeSchemaId = typeRecord.getSchemaId();
            final var typeSchemaRecord = descriptionTypeSchemaRepository.findById(typeSchemaId)
                    .orElseThrow(() -> new RuntimeException("Description type schema id not found: %d"));

            final var language = languageService.findById(record.getLanguageId());

            descriptions.add(descriptionFactory.create(record, typeRecord.getUri(), typeSchemaRecord.getUri(), language));
        }

        return descriptions;
    }
}
