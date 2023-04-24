There's many different ways and kinds of load-test, as many different kinds of
load-test as there are reasons to load-test.

Every different kind of load-test has a different goal or purpose.

### Establish baseline behaviour under average load 

Here, you only "load" the system to your baseline stead-state.
You're not looking to stress the system, just run a steady amount of load
at the expected "average usage".  You're not worried about peak throughput,
nor are you worried about minimum throughput.

You're not real "testing" anything, just establishing baseline numbers that
you will use in on-going regression tests.

### Regression testing behaviour of new releases

This is where you run the same load as your baseline load-test and looks for 
regressions.

* does the response time of any endpoints take longer now?
  * is that expected?
    * might be unexpected
      * left debug code in
      * bad design 
      * bad implementation
    * might expected
      * added validation via a new external service
        * increased response time for that endpoint probably just has to be 
        accepted
        * modify the baseline for the "new normal"
      * make sure new response time is documented in ChangeControl issue, 
        especially where it changes the ongoing cost profile 
* does the resource utilisation change?
  * change in infrastructure?
  * was it expected?
    * unexpected
      * implementation or configuration mistake
    * expected
      * optimisation
      * removal of functionality


### Stress test to breakage - establish baseline

This is where you find out exactly where your system breaks.


### Stress test regression of new release

Load to previous breaking point - does it break earlier now?


### Baseline and load test to "peak usage"


### Baseline and load test "demand reduction" scenario

Important in dynamic scaling environments.

After you load the system to "peak usage", it likely increases resource usage.

When load returns to "average usage", or even "minimal usage" - does the 
resource usage return to baseline?

This can be important even outside dynamic cloud environments.
Example, in a JVM environment:
* does your heap usage go back down?
* connection pools?
* garbage collection?
* if JVM internals and other resources are "scaled up" for max-throughput, that
can result in worse response times even once your system has transitioned to
a "average/minimal load" scenario
  * if, for example, the JVM retains the heap "just in case" (which earlier 
    JVMs are known to do) - that can affect other systems usage of the 
    shared resources
    * examples:
      * many systems all running on the same snowflake machine
      * many systems sharing machine resources via a container approach (ECS,
        Kubernetes, etc.) 


### Soak testing

Run a given load profile (usually average or peak) for as long as possible to 
establish if there's any degradation over time.
* memory leaks
* file handle leaks
* DB connection leaks
* poorly indexed queries
* poor algorithm choices
* general implementation bugs

