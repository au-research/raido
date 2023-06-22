Generally speaking, we will have INFO enabled in deployment environments
and DEBUG us only used for local development.

If there are issues that need to be resolved in an environment, we may
temporarily enable DEBUG (usually for a specific feature/function, rather 
than turning all debugging output on).

In this way - it's Ok for DEBUG stuff to be fairly verbose, because it's not
expected to be turned on in most deployment environments where it would cause
issues (too much noise in the logs so that they are hard to make sense of, 
an the logs themselves are expensive to log, store and query).

Raido code uses a simple wrapper around the SLF4J class to enable simple
structured logging that is suitable for our purposes - 
[Log.java](../spring/src/main/java/raido/apisvc/util/Log.java).

The current logging infrastructure target is AWS CLoudWatch, so it's best to 
keep logs on a single line. Cloudwatch doesn't have the fancier pattern 
matching and multi-line support of things like Splunk. 


### Special logging features
* [P6SpyLogger](../spring/src/main/java/raido/apisvc/util/logger/P6SpyLogger.java)
  * for logging SQL, transactions, etc.
  * you can also switch on pgjdbc DB driver logs, but those aren't very 
    readable 
* [StartupListener](../spring/src/main/java/raido/apisvc/spring/StartupListener.java)
  * for startup logging: JVM switches, etc.
* [ApidsService](../spring/src/main/java/raido/apisvc/service/apids/ApidsService.java)
  * allows logging just the calls to the APIDS service
  * `...ApidsService.http` category for logging calls to APIDS
* [RequestLoggingFilter](../spring/src/main/java/raido/apisvc/spring/RequestLoggingFilter.java)
  * logging of timings and user for endpoint calls
  * `...RequestLoggingFilter` category that does info logging of endpoint calls
  * `...RequestLoggingFilter.body` category that does debug logging of request 
    payload
* [AuthorizationFailureListener](../spring/src/main/java/raido/apisvc/spring/AuthorizationFailureListener.java)
  * explicit logging of authorization failures 
* [RedactingExceptionResolver](../spring/src/main/java/raido/apisvc/spring/RedactingExceptionResolver.java)
  * main purpose is to remove messages from unhandled exceptions to avoid 
    leaking data, but is also the main point where we log exception errors
* [RaidWebSecurityConfig.requestRejectedHandler()](../spring/src/main/java/raido/apisvc/spring/config/RaidWebSecurityConfig.java)
  * customised logging for rejected requests 
  * really early rejections don't make it to the other infrastructure
    * e.g. StrictFirewall failures
* [SLF4JBridgeHandler.install()](../spring/src/main/java/raido/apisvc/Api.java)
  * project also uses the SLF4J to capture standard JDK logging from 
    `java.util.logging`  
  * logs from pgjdbc - detailed db driver logging
  * jdk / java / sun stuff - encryption, networking, etc.
  * the JUL logging can be configured using the standard log file 
    [logback.xml](../spring/src/main/resources/logback.xml), or the usual 
    override mechanisms. 

