package au.org.raid.api.service.stub.ror;

import au.org.raid.api.service.ror.RorService;
import au.org.raid.api.spring.config.environment.InMemoryStubProps;
import au.org.raid.api.util.Log;

import java.util.List;

import static au.org.raid.api.service.stub.InMemoryStubTestData.NONEXISTENT_TEST_ROR;
import static au.org.raid.api.spring.bean.LogMetric.VALIDATE_ROR_EXISTS;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.ObjectUtil.areEqual;
import static au.org.raid.api.util.ObjectUtil.infoLogExecutionTime;
import static au.org.raid.api.util.ThreadUtil.sleep;
import static java.util.Collections.emptyList;
import static java.util.List.of;

public class InMemoryRorServiceStub extends RorService {
  private static final Log log = to(InMemoryRorServiceStub.class);
  
  private InMemoryStubProps stubProps;
  
  public InMemoryRorServiceStub(
    InMemoryStubProps stubProps
  ) {
    super(null);
    this.stubProps = stubProps;
  }

  @Override
  public List<String> validateRorExists(String ror) {
    log.with("delay", stubProps.rorInMemoryStubDelay).
      debug("simulate ROR validation check");
    infoLogExecutionTime(httpLog, VALIDATE_ROR_EXISTS, ()->{
      sleep(stubProps.rorInMemoryStubDelay);
      return null;
    });

    if( areEqual(ror, NONEXISTENT_TEST_ROR) ){
      return of(NOT_FOUND_MESSAGE);
    }
    
    return emptyList();
  }

}
