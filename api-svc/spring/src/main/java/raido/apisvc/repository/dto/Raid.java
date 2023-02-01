package raido.apisvc.repository.dto;

import raido.db.jooq.api_svc.tables.records.RaidRecord;
import raido.db.jooq.api_svc.tables.records.ServicePointRecord;

public record Raid(
  RaidRecord raid,
  ServicePointRecord servicePoint
) {
  @Override
  public RaidRecord raid() {
    return raid;
  }

  @Override
  public ServicePointRecord servicePoint() {
    return servicePoint;
  }
}
