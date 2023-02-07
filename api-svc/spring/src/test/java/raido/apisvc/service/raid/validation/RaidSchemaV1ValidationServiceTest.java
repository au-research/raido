package raido.apisvc.service.raid.validation;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.idl.raidv2.model.MintRequestSchemaV1;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RaidSchemaV1ValidationServiceTest {

  @InjectMocks
  private RaidSchemaV1ValidationService validationService;


  @Test
  void validateMintRequestAddsFailureIfServicePointNull() {
    var mintRequest = new MintRequestSchemaV1();

    var failures = validationService.validateMintRequest(mintRequest);

    assertThat(failures.size(), Matchers.is(1));
    assertThat(failures.get(0).getFieldId(), Matchers.is("mintRequest.servicePointId"));
    assertThat(failures.get(0).getMessage(), Matchers.is("field must be set"));
    assertThat(failures.get(0).getErrorType(), Matchers.is("notSet"));
  }
}