package raido.apisvc.service.stub.util;

import org.junit.jupiter.api.Test;
import raido.apisvc.util.Log;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static raido.apisvc.util.Log.to;

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
    
    generated.subList(0, 20).forEach(i->
      log.with("generated", i).info("first(20)")
    );
  }
}
