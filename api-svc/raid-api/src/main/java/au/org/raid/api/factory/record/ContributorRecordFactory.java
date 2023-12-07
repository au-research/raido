package au.org.raid.api.factory.record;

import au.org.raid.api.service.ContributorSchemaService;
import au.org.raid.db.jooq.tables.records.ContributorRecord;
import au.org.raid.idl.raidv2.model.Contributor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContributorRecordFactory {
    private final ContributorSchemaService contributorSchemaService;
    public ContributorRecord create(final Contributor contributor) {
        final var schemaId = contributorSchemaService.findSchemaIdByUri(contributor.getSchemaUri());

        return new ContributorRecord()
                .setPid(contributor.getId())
                .setSchemaId(schemaId);
    }
}
