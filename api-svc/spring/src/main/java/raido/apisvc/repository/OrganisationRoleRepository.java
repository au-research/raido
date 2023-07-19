package raido.apisvc.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import raido.db.jooq.api_svc.tables.records.OrganisationRoleRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.OrganisationRole.ORGANISATION_ROLE;

@Repository
public class OrganisationRoleRepository {
  private final DSLContext dslContext;

  public OrganisationRoleRepository(final DSLContext dslContext) {
    this.dslContext = dslContext;
  }

  public Optional<OrganisationRoleRecord> findByUriAndSchemeId(final String uri, final int schemeId) {
    return dslContext.select(ORGANISATION_ROLE.fields())
      .from(ORGANISATION_ROLE)
      .where(ORGANISATION_ROLE.URI.eq(uri)
        .and(ORGANISATION_ROLE.SCHEME_ID.eq(schemeId)))
      .fetchOptional(record -> new OrganisationRoleRecord()
        .setSchemeId(ORGANISATION_ROLE.SCHEME_ID.getValue(record))
        .setUri(ORGANISATION_ROLE.URI.getValue(record))
      );
  }
}
