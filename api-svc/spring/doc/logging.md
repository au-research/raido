Generally speaking, we willl have INFO enabled in deployment environments
and DEBUG us only used for local development.

If there are issues that need to be resolved in an enviornment, we may
temporarily enable DEBUG (usually for a specific feature/function, rather 
than turning all debugging output on).

In this way - it's Ok for DEBUG stuff to be fairly verbose, because it's not
expected to be turned on in most deployment enviornments where it would cause
issues (too much noise in the logs so that they are hard to make sense of, 
an the logs themselves are expensive to log, store and query).

### Special logging features
* [P6SpyLogger](../src/main/java/raido/util/logger/P6SpyLogger.java)
  * for logging SQL, transactions, etc.
  * you can also switch on pgjdbc DB driver logs, but those aren't very 
    readable 
* [StartupListener](../src/main/java/raido/spring/StartupListener.java)
  * for startup logging: JVM switches, etc.
* [ApidsService](../src/main/java/raido/service/apids/ApidsService.java)
  * allows logging just the calls to the APIDS service
  * `...ApidsService.http` category for logging calls to APIDS
* [RequestLoggingFilter](../src/main/java/raido/spring/RequestLoggingFilter.java)
  * logging of timings and user for endpoint calls
  * `...RequestLoggingFilter` category that does info logging of endpoint calls
  * `...RequestLoggingFilter.body` category that does debug logging of request 
    payload
* [AuthorizationFailureListener](../src/main/java/raido/spring/AuthorizationFailureListener.java)
  * explicit logging of authorization failures 
* [RedactingExceptionResolver](../src/main/java/raido/spring/RedactingExceptionResolver.java)
  * main purpose is to remove messages from unhandled exceptions to avoid 
    leaking data, but is also the main point where we log exception errors
* [RaidV1WebSecurityConfig.requestRejectedHandler()](../src/main/java/raido/spring/config/RaidV1WebSecurityConfig.java)
  * customised logging for rejected requests 
  * really early rejections don't make it to the other infrastructure
    * e.g. StrictFirewall failures
* [SLF4JBridgeHandler.install()](../src/main/java/raido/Api.java)
  * project also uses the SLF4J-JUL bridge to capture logs from pgjdbc 
    (detailed db driver logging) and JDK stuff (encryption, some networking)