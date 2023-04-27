package raido.apisvc.service.stub.orcid;

import raido.apisvc.service.orcid.OrcidService;
import raido.apisvc.spring.config.environment.InMemoryStubProps;
import raido.apisvc.util.Log;

import java.util.List;

import static java.util.Collections.emptyList;
import static raido.apisvc.spring.bean.LogMetric.VALIDATE_ORCID_EXISTS;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.infoLogExecutionTime;
import static raido.apisvc.util.ThreadUtil.sleep;

public class InMemoryOrcidServiceStub extends OrcidService {
  private static final Log log = to(InMemoryOrcidServiceStub.class);
  
  private InMemoryStubProps stubProps;
  
  public InMemoryOrcidServiceStub(
    InMemoryStubProps stubProps
  ) {
    super(null);
    this.stubProps = stubProps;
  }

  @Override
  public List<String> validateOrcidExists(String orcid) {
    log.with("delay", stubProps.orcidInMemoryStubDelay).
      debug("simulate ORCID validation check");
    infoLogExecutionTime(httpLog, VALIDATE_ORCID_EXISTS, ()->{
      sleep(stubProps.orcidInMemoryStubDelay);
      return null;
    });
    
    return emptyList();
  }

}
