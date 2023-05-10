package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.IdBlockV2;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.List;

@Component
public class IdBlockV2ValidationService {
  public List<ValidationFailure> validateUpdateHandle(final String decodedHandle, final IdBlockV2 id) {
    return null;
  }
}
