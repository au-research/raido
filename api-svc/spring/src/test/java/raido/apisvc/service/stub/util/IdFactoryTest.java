package raido.apisvc.service.stub.util;

import org.junit.jupiter.api.Test;
import raido.apisvc.util.Log;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.first;
import static raido.apisvc.util.ObjectUtil.last;

public class IdFactoryTest {
  private static final Log log = to(IdFactoryTest.class);
  
  @Test
  public void doesNotGenerateDuplicates(){
    int loopCount = 100_000;
    var id = new IdFactory("unittest");
    
    // allocate full capacity so it doesn't re-size in the middle of the loop
    var generated = new ArrayList<String>(loopCount);
    
    for( int i = 0; i < loopCount; i++ ){
      generated.add( id.generateUniqueId() );
    }
    
    assertThat(generated).doesNotHaveDuplicates();
    
    /* observed a two orders of magnitude difference in execution speed between 
    first and last because of because of JVM JIT compilation and other hotspot 
    optimisation stuff.
    So the load-test, for example, needs to generate at least 200-300 mint
    operations per-second per-JVM - in order to reach this level of usage. 
    Very unlikely, even with all in-memory stubs; especially while we run dinky
    little toaster EC2 instances with minimal CPU, memory and network bandwidth,
    and a small DB instance. */
    first(generated, 20).forEach(i->
      log.with("generated", i).info("first(20)")
    );
    last(generated, 20).forEach(i->
      log.with("generated", i).info("last(20)")
    );
  }
}
