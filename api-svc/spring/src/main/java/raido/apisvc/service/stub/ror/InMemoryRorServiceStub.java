package raido.apisvc.service.stub.ror;

import raido.apisvc.service.ror.RorService;
import raido.apisvc.spring.config.environment.InMemoryStubProps;
import raido.apisvc.util.Log;

import java.util.List;

import static java.util.Collections.emptyList;
import static raido.apisvc.spring.bean.MetricBean.VALIDATE_ROR_EXISTS;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.infoLogExecutionTime;
import static raido.apisvc.util.ThreadUtil.sleep;

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
    
    return emptyList();
  }

}
