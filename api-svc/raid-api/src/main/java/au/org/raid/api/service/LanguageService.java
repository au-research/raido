package au.org.raid.api.service;

import au.org.raid.api.factory.LanguageFactory;
import au.org.raid.api.repository.LanguageRepository;
import au.org.raid.api.repository.LanguageSchemaRepository;
import au.org.raid.idl.raidv2.model.Language;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LanguageService {
    private final LanguageRepository languageRepository;
    private final LanguageSchemaRepository languageSchemaRepository;
    private final LanguageFactory languageFactory;

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

    public Language findById(final Integer id) {
        if (id == null) {
            return null;
        }
        final var languageRecord = languageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Language not found with id %d".formatted(id)));

        final var schemaId = languageRecord.getSchemaId();

        final var languageSchemaRecord = languageSchemaRepository.findById(schemaId)
                .orElseThrow(() -> new RuntimeException("Language schema not found with id %d".formatted(schemaId)));

        return languageFactory.create(languageRecord.getCode(), languageSchemaRecord.getUri());
    }
}
