package au.org.raid.api.service;

import au.org.raid.api.repository.LanguageRepository;
import au.org.raid.api.repository.LanguageSchemaRepository;
import au.org.raid.idl.raidv2.model.Language;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LanguageService {
    private final LanguageRepository languageRepository;
    private final LanguageSchemaRepository languageSchemaRepository;

    @Transactional(readOnly = true)
    public Integer findLanguageId(@Nullable final Language language) {
        if (language == null) {
            return null;
        }

        final var languageSchemaRecord = languageSchemaRepository.findByUri(language.getSchemaUri())
                .orElseThrow(() -> new RuntimeException("Language schema not found %s".formatted(language.getSchemaUri())));

        final var languageRecord = languageRepository.findByIdAndSchemaId(language.getId(), languageSchemaRecord.getId())
                .orElseThrow(() -> new RuntimeException("Language %s not found in schema %s".formatted(language.getId(), language.getSchemaUri())));

        return languageRecord.getId();
    }
}
