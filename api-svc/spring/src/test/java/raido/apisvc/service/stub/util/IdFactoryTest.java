package raido.apisvc.service.stub.util;

import org.junit.jupiter.api.Test;
import raido.apisvc.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Runtime.getRuntime;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.assertj.core.api.Assertions.assertThat;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.first;
import static raido.apisvc.util.ObjectUtil.last;

public class IdFactoryTest {
  private static final Log log = to(IdFactoryTest.class);
  
  @Test
  public void shouldNotGenerateDuplicatesWhenExecutedSequentially(){
    int loopCount = 200_000;
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

  /**
   How many may execute concurrently depends on your hardware.
   STO machine: Ryzen 3600 - 6 core, 12 thread (reports cores=12)
   GitHub Action: reports cores=2
   ChatGPT wrote this concurrent code - looks right but I wouldn't really know.
   On my machine the concurrent test runs a few hundreds millis faster.
   I'm taking that to mean that some concurrency is happening.
   It shouldn't really be much faster to execute, even on 12 thread hardware
   because the IdFactory.generateUniqueID() method is synchronised, that's the
   whole point of it.
   */
  @Test
  public void shouldNotGenerateDuplicatesWhenExecutedConcurrently(){
    int loopCount = 200_000;
    var id = new IdFactory("unittest");
    int numberOfThreads = 10;
    
    var exec = newFixedThreadPool(numberOfThreads);
    log.with("cores", getRuntime().availableProcessors()).
      with("vm.vendor", System.getProperty("java.vm.vendor")).
      with("vm.name", System.getProperty("java.vm.name")).
      with("vm.version", System.getProperty("java.vm.version")).
      info("current machine");
    
    // allocate full capacity so it doesn't re-size in the middle of the loop
    var generated = new ArrayList<String>(loopCount);
    var tasks = new ArrayList<>(loopCount);
    
    for( int i = 0; i < loopCount; i++ ){
      CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
        generated.add( id.generateUniqueId() );
      }, exec);

      tasks.add(task);      
      
    }

    CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();

    exec.shutdown();
    try {
      boolean termination = exec.awaitTermination(5, TimeUnit.SECONDS);
      log.with("result", termination).info("exec terminated");
    } catch (InterruptedException e) {
      System.err.println("ExecutorService termination was interrupted");
      Thread.currentThread().interrupt();
    }
    
    assertThat(generated).doesNotHaveDuplicates();
    
    first(generated, 20).forEach(i->
      log.with("generated", i).info("first(20)")
    );
    last(generated, 20).forEach(i->
      log.with("generated", i).info("last(20)")
    );
  }
}
