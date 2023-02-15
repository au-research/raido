package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RaidSchemaV1ValidationServiceTest {

  @InjectMocks
  private RaidSchemaV1ValidationService validationService;


  @Test
  void validateMintRequestAddsFailureIfServicePointNull() {
//    var mintRequest = new MintRequestSchemaV1();
//
//    var failures = validationService.validateMintRequest(mintRequest);
//
//    assertThat(failures.size(), Matchers.is(1));
//    assertThat(failures.get(0).getFieldId(), Matchers.is("mintRequest.servicePointId"));
//    assertThat(failures.get(0).getMessage(), Matchers.is("field must be set"));
//    assertThat(failures.get(0).getErrorType(), Matchers.is("notSet"));
  }
}