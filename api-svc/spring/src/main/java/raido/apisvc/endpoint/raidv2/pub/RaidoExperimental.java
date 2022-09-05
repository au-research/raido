package raido.apisvc.endpoint.raidv2.pub;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RestController;
import raido.idl.raidv2.api.RaidoExperimentalApi;
import raido.idl.raidv2.model.Institution;

import java.util.List;

import static org.jooq.impl.DSL.orderBy;
import static org.jooq.impl.DSL.rowNumber;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static raido.db.jooq.raid_v1_import.tables.Metadata.METADATA;

@Scope(proxyMode = TARGET_CLASS)
@RestController
public class RaidoExperimental implements RaidoExperimentalApi {

  private DSLContext db;

  public RaidoExperimental(
    DSLContext db
  ) {
    this.db = db;
  }

  @Override
  public List<Institution> listInstitutions() {
    return db.
      select(
        rowNumber().over(orderBy(METADATA.NAME)).as("id"),
        METADATA.NAME).
      from(METADATA).
      fetchInto(Institution.class);
  }

}
