package raido.apisvc.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import raido.db.jooq.api_svc.tables.records.DescriptionTypeRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.DescriptionType.DESCRIPTION_TYPE;

@Repository
@RequiredArgsConstructor
public class DescriptionTypeRepository {
  private final DSLContext dslContext;

  public Optional<DescriptionTypeRecord> findByUriAndSchemeId(final String uri, final int schemeId) {
    return dslContext.select(DESCRIPTION_TYPE.fields()).
      from(DESCRIPTION_TYPE).
      where(DESCRIPTION_TYPE.URI.eq(uri)).
      fetchOptional(record -> new DescriptionTypeRecord()
        .setSchemeId(DESCRIPTION_TYPE.SCHEME_ID.getValue(record))
        .setUri(DESCRIPTION_TYPE.URI.getValue(record))
      );
  }
}