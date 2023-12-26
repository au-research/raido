package au.org.raid.api.service;

import au.org.raid.api.factory.AccessFactory;
import au.org.raid.api.factory.AccessStatementFactory;
import au.org.raid.api.repository.AccessTypeRepository;
import au.org.raid.api.repository.AccessTypeSchemaRepository;
import au.org.raid.db.jooq.tables.records.RaidRecord;
import au.org.raid.idl.raidv2.model.Access;
import au.org.raid.idl.raidv2.model.AccessStatement;
import au.org.raid.idl.raidv2.model.AccessType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccessService {
    private final AccessTypeSchemaRepository accessTypeSchemaRepository;
    private final AccessTypeRepository accessTypeRepository;
    private final AccessFactory accessFactory;
    private final LanguageService languageService;
    private final AccessTypeService accessTypeService;
    private final AccessStatementFactory accessStatementFactory;

    public Integer findAccessTypeId(final Access access) {
        final var accessTypeSchemaUri = access.getType().getSchemaUri();
        final var accessTypeUri = access.getType().getId();

        final var accessTypeSchemaRecord = accessTypeSchemaRepository.findByUri(accessTypeSchemaUri)
                .orElseThrow(() -> new RuntimeException("Access type schema not found %s".formatted(accessTypeSchemaUri)));

        final var accessTypeRecord = accessTypeRepository.findByUriAndSchemaId(accessTypeUri, accessTypeSchemaRecord.getId())
                .orElseThrow(() -> new RuntimeException("Access type %s not found in schema %s".formatted(
                        accessTypeUri, accessTypeSchemaUri
                )));

        return accessTypeRecord.getId();
    }

    public Access getAccess(final RaidRecord record) {
        final var language = languageService.findById(record.getAccessStatementLanguageId());
        final var type = accessTypeService.findById(record.getAccessTypeId());
        final var statement = accessStatementFactory.create(record.getAccessStatement(), language);
        return accessFactory.create(statement, type, record.getEmbargoExpiry());
    }
}
