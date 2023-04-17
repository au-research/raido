package raido.apisvc.service.stub.apids;

import org.springframework.stereotype.Component;
import raido.apisvc.service.apids.ApidsService;
import raido.apisvc.service.apids.model.ApidsMintResponse;
import raido.apisvc.service.stub.util.IdFactory;
import raido.apisvc.spring.bean.MetricBean;
import raido.apisvc.spring.config.environment.ApidsProps;
import raido.apisvc.spring.config.environment.EnvironmentProps;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;

import java.util.function.Function;

import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.infoLogExecutionTime;
import static raido.apisvc.util.ThreadUtil.sleep;

/* When we want to load-test api-svc in a multi-node configuration (which is 
how it's configured in prod) - we'll need to add a "node-prefix" to the 
generated handle suffixes, so that they're unique.  
Use `nodeId` from EnvironmentProps, which will probably end up containing the 
ECS task-id (somehow!?).  Can't be ec2-instance-id anymore, since you can have 
multiple ECS tasks running on the same EC2 instance.
Not that IdFactory has a nodeId concept too - but that should probably be 
removed - it doesn't make sense at that level.

"InMemory" because eventually we'll need a proper standalone service, running on
 the other side of a network call (it'll still just be a stub though).
 
*/
@Component
public class InMemoryApidsServiceStub extends ApidsService {
  private static final Log log = to(InMemoryApidsServiceStub.class);
  
  private ApidsProps apidsProps;
  private EnvironmentProps envConfig;
  
  public InMemoryApidsServiceStub(
    ApidsProps props,
    EnvironmentProps envConfig
  ) {
    super(props, null);
    this.envConfig = envConfig;
    this.apidsProps = props;
  }

  public void onStartup() {
    Guard.isTrue("Cannot use InMemoryApidsService in a PROD env",
      !envConfig.isProd);

    log.warn("using the in-memory APIDs service");
  }

  @Override
  public ApidsMintResponse mintApidsHandleContentPrefix(
    Function<String, String> raidLandingPageUrl
  ) {
    ApidsMintResponse resp = new ApidsMintResponse();
    resp.type = "success";
    resp.identifier = new ApidsMintResponse.Identifier();

    // again, note: not unique if api-svc using multiple nodes
    resp.identifier.handle = "stubprefix/" + IdFactory.generateUniqueId(false);

    resp.identifier.property = new ApidsMintResponse.Identifier.Property();
    /* these are just copied from the example comment in ApidsMintResponse,
    just trying to get the mint process working. */
    resp.identifier.property.value = "RAID handle";
    resp.identifier.property.index = 1;
    resp.identifier.property.type = "DESC";

    log.with("delay", apidsProps.inMemoryStubDelay).
      with("handle", resp.identifier.handle).
      info("simulate APIDS mint request");
    infoLogExecutionTime(httpLog, MetricBean.APIDS_MINT_WITH_DESC, ()->{
      sleep(apidsProps.inMemoryStubDelay);
      return null;
    });

    log.with("delay", apidsProps.inMemoryStubDelay).
      info("simulate APIDS add request");
    infoLogExecutionTime(httpLog, MetricBean.APIDS_ADD_URL_VALUE, ()->{
      sleep(apidsProps.inMemoryStubDelay);
      return null;
    });
    
    return resp;
  }
}
