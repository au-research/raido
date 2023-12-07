package au.org.raid.api.service;

import au.org.raid.api.exception.ResourceNotFoundException;
import au.org.raid.api.repository.ContributorSchemaRepository;
import au.org.raid.db.jooq.tables.records.ContributorSchemaRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ContributorSchemaService {
    private final ContributorSchemaRepository contributorSchemaRepository;

    public int findSchemaIdByUri(final String uri) {
        return contributorSchemaRepository.findByUri(uri)
                .map(ContributorSchemaRecord::getId)
                .orElseThrow(() -> new RuntimeException("Schema not found"));
    }
}
