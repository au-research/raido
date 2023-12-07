package au.org.raid.api.service;

import au.org.raid.api.factory.record.RaidTitleRecordFactory;
import au.org.raid.api.repository.RaidTitleRepository;
import au.org.raid.api.repository.TitleTypeRepository;
import au.org.raid.api.repository.TitleTypeSchemaRepository;
import au.org.raid.idl.raidv2.model.Title;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TitleService {
    private final RaidTitleRepository raidTitleRepository;
    private final TitleTypeRepository titleTypeRepository;
    private final TitleTypeSchemaRepository titleTypeSchemaRepository;
    private final LanguageService languageService;
    private final RaidTitleRecordFactory raidTitleRecordFactory;

    public void create(final List<Title> titles, final String raidName) {

        for (final var title : titles) {
            final var titleTypeSchema = titleTypeSchemaRepository.findByUri(title.getType().getSchemaUri())
                    .orElseThrow(() ->
                            new RuntimeException("Title type schema not found %s".formatted(title.getType().getSchemaUri())));

            final var titleType = titleTypeRepository.findByUriAndSchemaId(title.getType().getId(), titleTypeSchema.getId())
                    .orElseThrow(() -> new RuntimeException("Title type %s not found in schema %s".formatted(title.getType().getId(), title.getType().getSchemaUri())));

            final var languageId = languageService.findLanguageId(title.getLanguage());

            final var raidTitleRecord = raidTitleRecordFactory.create(title, raidName, titleType.getId(), languageId);

            raidTitleRepository.create(raidTitleRecord);
        }
    }
}
