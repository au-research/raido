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
* [StartupListener](../src/main/java/raido/spring/StartupListener.java)
  * for startup logging
* [ApidsService](../src/main/java/raido/service/apids/ApidsService.java)
  * `httpLog` category for logging calls to APIDS
* [LoggingFilter](../src/main/java/raido/spring/LoggingFilter.java)
  * logging of timings and user for endpoint calls
  * `bodyLog` debug logging of request payload, etc.