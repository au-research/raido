package raido.apisvc.service.stub.doi;

import raido.apisvc.service.doi.DoiService;
import raido.apisvc.spring.config.environment.InMemoryStubProps;
import raido.apisvc.util.Log;

import java.util.List;

import static java.util.Collections.emptyList;
import static raido.apisvc.spring.bean.LogMetric.VALIDATE_DOI_EXISTS;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.infoLogExecutionTime;
import static raido.apisvc.util.ThreadUtil.sleep;

public class InMemoryDoiServiceStub extends DoiService {
  private static final Log log = to(InMemoryDoiServiceStub.class);

  private InMemoryStubProps stubProps;

  public InMemoryDoiServiceStub(
    InMemoryStubProps stubProps
  ) {
    super(null);
    this.stubProps = stubProps;
  }

  @Override
  public List<String> validateDoiExists(String doi) {
    log.with("delay", stubProps.doiInMemoryStubDelay).
      debug("simulate DOI validation check");
    infoLogExecutionTime(httpLog, VALIDATE_DOI_EXISTS, ()->{
      sleep(stubProps.doiInMemoryStubDelay);
      return null;
    });
    
    return emptyList();
  }
}
