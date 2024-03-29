# app-client

## app-client routing needs a lot of work

The NavigationProvider doesn't understand query parameters, only pathname.
The implementation of pathname parameters is a hack - I ran into the problem,
didn't want to get side-tracked but still wanted have routable urls with params.

Look at the multiple url stuff for supporting creating and editing on 
ApiKeyPage.


## should not use the main app-client for presenting the raid landing page

It's way bigger (200+ KB) than necessary.  I think it would be better to have 
that specific page generated using a completely different technology. 
The page should be mostly static, it doesn't need interactivity.

I think the page should be pre-generated, or maybe we should serve it through
a lambda that can use whatever tech it wants (maybe still React, but it 
generates the HTML rather than running on the client).


# api-svc

## Implement a "deny-by-default" authorization scheme

Currently, the API endpoints are "deny-by-default" to un-authenticated users,
if you're not authenticated, then you won't be able to call any endpoint unless
that endpoint has been specifically declared to be publicly accessible (by 
placing it under the `/v2/public` prefix).

That means anyone with any role (Operator, Admin, or User) can access an 
endpoint if the developer hasn't implemented authz checking.

Two approaches I've thought of:
* an implementation approach - add a "I have checked authz flag", probably in 
  the SecurityContext and if that flag never got set in the implementation, 
  abandon the endpoint call (what about transaction rollback?, what about 
  side effect stuff done with other systems?)
* a test-based approach 
  * iterate all non-public endpoints (easy)
  * somehow magically figure out if developer implemented authz  


## JWT authorisation validation causes too much DB load

At the moment, every endpoint reads from the app_user table for every 
single authorized endpoint call, every time.  This is done in order
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
app_user data (using the Caffeine library) with an aggressive cache of a few 
seconds.  This cache is not about latency/uptime/reliability - it's purely to 
relieve the DB (and local connection pool) of the processing/connection 
overhead of all those user queries. 

Long-term, this could further be improved with a dedicated caching service
such as Redis/ElasticCache.  Though at that point it likely makes more sense
to factor out the app_user stuff to a separate IDService that can control its
own fate with regard to caching/DB technology used.


## IDL projects generate java of the from `Void endpointName()`   

When we have a POST endpoint that doesn't return anything, OpenAPI generates a 
method with the `Void` return type, from which we then have to `return null`.

This happens because we customise the return type of OpenAPI generated methods
to not wrap return values in a `ResponseEntity`.  
Returning `ResponseEntity<Void>` makes sense, returning `Void` is silly.
We should be generating methods that just return `void` in this case.

This might not be worth fixing though.  Most endpoints will end up returning a 
generic "success/fail" type that can return info about the failure. So the 
awkwardness with `Void` may not turn out to be much of an issue.


## Current API is very "un-REST-ful"

We need a "REST API" guidelines policy, similar to the 
[DB schema guidelines](/api-svc/db/raido/doc/schema-guideline.md).

Randomly structured endpoints for experimental stuff is fine - but we need to 
be more rigorous for the stable endpoints.

I've never actually seen an actual, concrete, *practical* 
"right way to do REST endpoints" document. 
I need a concrete set of guidelines to conform to - that  can reasonably be 
implemented in the context of our tooling (openapi-generator).


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

## public API endpoints need CORS headers

Mostly we're expecting non-browser calling contexts for the public API endpoints
(i.e. curl clients, proper application integration on the backend).
But do we want to add CORS headers in case people want to call from browser?
Like I want to do for orcid?
Do we really want to enable this?  Our API will get abused like hell (just like
I want to abuse the orcid API).

# AWS / Infra

AWS tech debt is documented in that repo, don't put it here.


