package au.org.raid.api.spring.config.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InMemoryStubProps {
  @Value("${Apids.inMemoryStub:false}")
  public boolean apidsInMemoryStub;

  @Value("${Apids.inMemoryStubDelay:150}")
  public long apidsInMemoryStubDelay;

  @Value("${Orcid.inMemoryStub:false}")
  public boolean orcidInMemoryStub;

  @Value("${Orcid.inMemoryStubDelay:150}")
  public long orcidInMemoryStubDelay;

  @Value("${Ror.inMemoryStub:false}")
  public boolean rorInMemoryStub;

  @Value("${Ror.inMemoryStubDelay:150}")
  public long rorInMemoryStubDelay;

  @Value("${Doi.inMemoryStub:false}")
  public boolean doiInMemoryStub;

  @Value("${Doi.inMemoryStubDelay:150}")
  public long doiInMemoryStubDelay;
}
