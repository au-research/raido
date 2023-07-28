package raido.apisvc.service.raid;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import raido.db.jooq.api_svc.tables.records.RaidRecord;
import raido.idl.raidv2.model.RaidDto;
import raido.idl.raidv2.model.UpdateRaidV1Request;

@Component
@RequiredArgsConstructor
public class RaidChecksumService {
  private final ObjectMapper objectMapper;

  @SneakyThrows
  public String create(final RaidRecord raid) {
    final var metadata = raid.getMetadata().data();
    // A bit nasty to turn a string into an object and then back into a string but the json needs to be in the
    // same order for the checksums to be identical. This relies on MapperFeature.SORT_PROPERTIES_ALPHABETICALLY
    // property set on the ObjectMapper bean

    final var raidDto = objectMapper.readValue(metadata, RaidDto.class);
    final var json = objectMapper.writeValueAsString(raidDto);

    return DigestUtils.md5DigestAsHex(json.getBytes());
  }

  @SneakyThrows
  public String create(final UpdateRaidV1Request raid) {
    return DigestUtils.md5DigestAsHex(objectMapper.writeValueAsString(raid).getBytes());
  }

}