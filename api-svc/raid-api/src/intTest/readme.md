
### Running integration tests

You must have a "raido" database, as per [db/readme.md](../../../db/readme.md)
and the api-svc must be configured as per [api-svc/readme.md](../../readme.md).

The intTests will pick up that api-svc-env.properties configuration, so you 
shouldn't need any extra config by default.

Note that, by default, the intTests use the same database that your local
API server uses.  This can cause problems if you're running them a the same 
time - so the intTests check the port and refused to run if there's already 
a server running on that same port.  The intent is to stop the developer 
from accidentally running the intTests while they are still running a local
server - it also serves to avoid accidentally running the intTests in 
parallel.

The intTests assume they are the only thing running on that DB:
* it makes writing re-runnable tests easier
* it avoids DB queues "stealing" work from each other - which is fine in prod
  but violates out intTest assumption
* but it does mean that intTests can't be run in parallel


### Configuration

Remember, there are two completely separate Spring application contexts:

#### int-test Spring context
* `./java/inttest.properties`
  * default values for various int-test config items
  * only values that are allowed to be committed (i.e. not secrets)
* `~/.config/raido/api-svc-inttest.properties`
  * env specific config that inttests need that we do not want to commit
  * e.g. `raidoArdcLiveToken` value 
* `~/.config/raido/api-svc-env.properties`
  * the standard api-svc "local dev environment" config file
  * int-tests read this so they can connect to the same DB service used by
  the api-svc being tested against (to bootstrap API tokens)


#### api-svc Spring context

* `./java/env.properties`
  * override api-svc values used by default in int-tests
  * used where it's expected that int-tests want to override these values in 
  most environments
  * only values that are allowed to be committed (i.e. not secrets)
* `~/.config/raido/api-svc-env.properties`
  * the standard api-svc "local dev environment" config file

