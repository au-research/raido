# app-client

## `/app-client` is a standalone gradle project

See the "Gradle build" section of the 
[/app-client/readme.md](/app-client/readme.md).

Want to re-do the versioning.
Current plan:
* replace the `git-version` plugin with some simple imperative code to 
lookip the version from `git describe` and commit id.
  * can just try to invoke `git` at the command line
* allow the ability to pre-empt the lookup with an env var - so you can
still have stuff work inside `codepipeline`
  * passing in the commit id will be easy, probably have to go `git descrive`
  based functionality.  That's ok, because we don't use a version for the 
  cpp-client - it would be meaningless.
* should still fast fail though, i.e. 
  * first look for a provided version/commit id in the env vars
  * then try to invoke `git` at the command line
  * probably not a good idea to have a "unknown" fallback
    * dev should get feedback immediately if they're building an environment 
    and the links to source code / version aren't going to work
    * want to avoid _accidentally_ building environments/artifacts that don't
    track their version properly
    * if the dev really wants, they can force it by setting the env vars
      to "unknown" values

## app-client routing needs a lot of work

The NavigationProvider doesn't understand query parameters, only pathname.
The implementation of pathname parameters is a hack - I ran into the problem,
didn't want to get side-tracked but still wanted have routable urls with params.

Look at the multiple url stuff for supporting creating and editing on 
ApiKeyPage.


## app-client error handling needs work

The error display page (ErrorDialog, not CompactErrorPanel) needs to provide
better user instructions and components for users to get out of error states
caused by stuff like:
* token being expired - the authcontext picks this up when loading the page,
  but we're not dealing with the page being open for N hours.
  * need token refresh logic
  * or at least a sign-out/refresh button
* some change in server data causing unexpected issues
  * user is currently on an SP_ADMIN page, but has been demoted to SP_USER
causing data synch issue)

Might be a good idea to do this in concert with fixing up the api-svc error
handling.


## should not use the main app-client for presenting the raid landing page

It's way bigger than necessary.  I think it would be better to have that 
specific page generated using a completely different technology. 
The page should be mostly static, it doesn't need interactivity.

I think the page should be pre-generated, or maybe we should serve it through
a lambda that can use whatever tech it wants (maybe still React, but it 
generates the HTML rather than running on the client).


# api-svc

## Implement a "deny-by-default" authorization scheme

Currently, the API endpoints are "deny-by-default" to un-authenticated users,
if you're not authenticated, then you won't be able to call any endpoint unless
that endpoint has been specifically declared to be pulicly accessible (by 
placing it under the `/v2/public` prefix).

That means anyone with any role (Operator, Admin, or User) can access an 
endpoint if the developer hasn't implemented authz checking.

Two approaches I've thought of:
* an implementation approach - add a "I have checked authz flag", probably in 
  the SecurityContext and if that flag never got set in the implmentation, 
  abandon the endpoint call (what about transaction rollback?, what about 
  side effect stuff done with other systems?)
* a test-based approach 
  * iterate all non-public endpoints (easy)
  * somehow magically figure out if developer implmented authz  


## JWT authorisation validation causes too much DB load

At the moment, every endpoint reads from the app_user table for every 
single authroizable endpoint call, every time.  This is done in order
to validate that the user/token represented by the token:
* had not been disabled 
* has not been "blacklisted" via token cutoff

Note that this read is *not* about reading user role or other future authz info
- we trust the signing of the bearer token to ensure validity of that info
(though it could be out of date).

This will cause too many redundant queries to be issued on the prod DB under 
load (most of the time, the user hasn't changed and the queries are completely 
unnecessary).

In the medium-term, we will alleviate this load by caching the relevant 
app_user data (using the Caffeine libarary) with an agressive cache of a few 
seconds.  This cache is not about latency/uptime/reliabilty - it's purely to 
relieve the DB (and local connection pool) of the processing/connection 
overhead of all those user queries. 

Long-term, this could further be improved with a dedicated caching service
such as redis/elasticache.  Though at that point it likely makes more sense
to factor out the app_user stuff to a separate IDService that can control its
own fate with regard to caching/DB technology used.


## IDL projects generate java of the from `Void entpointName()`   

When we have a POST endpoint that doesn't return anything, we end with a method
with the `Void` return type, which we then have to `return null` from, ick.

This happens because we customise the return type of OpenAPI generated methods
to not wrap return values in a `ResponseEntity`.  
Returning `ResponseEntity<Void>` makes sense, returning `Void` is silly.
We should be generating methods that just return `void` in this case.

This might not be worth fixing though.  Most endpoints will end up returning a 
generic "sucess/fail" type that can return info about the failure. So the 
awkwardness with `Void` may not turn out to be much of an issue.


## Current API is very "un-RESTful"

We need a "REST API" guidelines policy, similar to the 
[DB schema guidlines](../api-svc/db/raido/doc/schema-guideline.md).

Randomly structured endpoints for experimental stuff is fine - but we need to 
be more rigorous for the stable endpoints.

I've never actually seen an actual, concrete, *practical* 
"right way to do REST endpoints" document. 
I need a concrete set of guidelines to conform to - that  can reasonably be 
implemented in the context of our tooling (openapi-generator).

## Need endpoint pagination

But want to avoid usage of `limit` / `offset`.
https://use-the-index-luke.com/blog/2013-07/pagination-done-the-postgresql-way

Start with the `list-raid` endpoint.


## APIDS service needs refactoring

It's very much a "just get it working" implementation at the moment.

Should probably try to use Feign or some other proper client instead of 
the current dodgy hardcoded/hand-coded XML RestTemplate implementation.


## Exception handling - define a global strategy

See class comment on RedactingExceptionResolver


## ExceptionUtil - unclear method names

ExceptionUtil has method names like `ise()` and `iae()` these are clear to 
me because I'm used to talking about NPE/IAE/ISE, but I think too short.
Change to `illegalState()`.  The "exception" part of the name is implied given
the class name and that's short enough - we don't _need_ the extreme shortness
of the initialisms.

## Mapping methods - unclear method names

There's various methods of the from `js2jq()` all over the place.
It stands for "Javascript to Jooq", but that's a fairly obtuse naming scheme.
Change to `api2Db()` and `db2Api()`, it's one extra char and a lot clearer
about what's going on.

## Versioning needs to be re-done

I still want to use a `git describe` approach for versioning, but need to
get rid of the palantir plugin and make the git string optionally come from
the environment (for AWS CI/CD stuff that doesn't provide the repo).

That said, how can we use `git describe` functionality for the AWS stuff?
I don't think we can :(


# AWS / Infra

AWS tech debt is documented in that repo, don't put it here.


