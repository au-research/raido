package raido.apisvc.service.raid;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import raido.db.jooq.api_svc.tables.records.RaidRecord;
import raido.idl.raidv2.model.UpdateRaidV1Request;

@Component
@RequiredArgsConstructor
public class RaidChecksumService {
  private final ObjectMapper objectMapper;

  public String create(final RaidRecord raid) {
    return DigestUtils.md5DigestAsHex(raid.getMetadata().data().getBytes());
  }

  @SneakyThrows
  public String create(final UpdateRaidV1Request raid) {
    return DigestUtils.md5DigestAsHex(objectMapper.writeValueAsString(raid).getBytes());
  }

}