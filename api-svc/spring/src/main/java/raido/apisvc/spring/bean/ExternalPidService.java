package raido.apisvc.spring.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import raido.apisvc.service.apids.ApidsService;
import raido.apisvc.service.doi.DoiService;
import raido.apisvc.service.orcid.OrcidService;
import raido.apisvc.service.ror.RorService;
import raido.apisvc.service.stub.apids.InMemoryApidsServiceStub;
import raido.apisvc.service.stub.doi.InMemoryDoiServiceStub;
import raido.apisvc.service.stub.orcid.InMemoryOrcidServiceStub;
import raido.apisvc.service.stub.ror.InMemoryRorServiceStub;
import raido.apisvc.spring.config.environment.ApidsProps;
import raido.apisvc.spring.config.environment.EnvironmentProps;
import raido.apisvc.spring.config.environment.InMemoryStubProps;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;

import static raido.apisvc.util.Log.to;

@Component
public class ExternalPidService {
  private static final Log log = to(ExternalPidService.class);
  
  @Bean
  @Primary
  public ApidsService apidsService(
    EnvironmentProps envConfig,
    InMemoryStubProps stubProps,
    ApidsProps apidsConfig,
    RestTemplate rest
  ){
    /* IMPROVE: I'm fairly sure I'm not doing this the "spring way" */
    if( stubProps.apidsInMemoryStub ){
      Guard.isTrue("Cannot use InMemoryApidsServiceStub in a PROD env",
        !envConfig.isProd);
      log.with("apidsInMemoryStubDelay", stubProps.apidsInMemoryStubDelay).
        warn("using the in-memory ORCID service");
      return new InMemoryApidsServiceStub(stubProps, envConfig);
    }

    /* now we aren't forced to set the secret if we're not using the real 
    APIDS service - unexpected benefit! */
    Guard.allHaveValue("must set ApidsProps values",
      apidsConfig.secret, apidsConfig.appId, apidsConfig.serviceUrl);
    return new ApidsService(apidsConfig, rest);
  }

  @Bean @Primary
  public OrcidService orcidService(
    EnvironmentProps envConfig,
    InMemoryStubProps stubProps,
    RestTemplate rest
  ){
    if( stubProps.orcidInMemoryStub ){
      Guard.isTrue("Cannot use InMemoryOrcidServiceStub in a PROD env",
        !envConfig.isProd);
      log.with("orcidInMemoryStubDelay", stubProps.orcidInMemoryStubDelay).
        warn("using the in-memory ORCID service");
      return new InMemoryOrcidServiceStub(stubProps);
    }

    return new OrcidService(rest);
  }

  @Bean @Primary
  public RorService rorService(
    EnvironmentProps envConfig,
    InMemoryStubProps stubProps,
    RestTemplate rest
  ){
    if( stubProps.rorInMemoryStub ){
      Guard.isTrue("Cannot use InMemoryRorServiceStub in a PROD env",
        !envConfig.isProd);
      log.with("rorInMemoryStubDelay", stubProps.rorInMemoryStubDelay).
        warn("using the in-memory ROR service");
      return new InMemoryRorServiceStub(stubProps);
    }

    return new RorService(rest);
  }


  @Bean @Primary
  public DoiService doiService(
    EnvironmentProps envConfig,
    InMemoryStubProps stubProps,
    RestTemplate rest
  ){
    if( stubProps.doiInMemoryStub ){
      Guard.isTrue("Cannot use InMemoryDoiServiceStub in a PROD env",
        !envConfig.isProd);
      log.warn("using the in-memory DOI service");
      return new InMemoryDoiServiceStub(stubProps);
    }

    return new DoiService(rest);
  }  
}
