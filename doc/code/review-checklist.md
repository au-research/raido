List of things to consider in a code review.


# Security

## Are endpoints secure?

Endpoint implementations must check authorization biz rules.


## No secrets in code

No tokens or any kind of sensitive data in the repo


## Don't log sensitive data

Not even as "debug".


# Performance

## API Pagination

List-type endpoints must implement pagination, or implement a hard maximum 
limit on the number of items returned.


## Consider the SQL being issued

* avoid n+1 queries
* avoid returning too many unused columns


## Consider if queries will use an index

* will the query use an existing index? Does an index need to be modified/added?


# Compatibility

## Avoid incompatible changes to stable API endpoints 

If an incompatible change is needed, a new version of the data structure should
be published on a new version of the endpoint.


## Avoid incompatible changes to the DB schema

If an incompatible change is needed, it should be split up into multiple changes
so that a "zig zag" deployment flow can be implemented.


## Consider forward compatibility on stable API versions

Example: if creating a list, don't just return that list.
Return the list inside an object with a "list" field, so that other fields
can be added (like pagingation).


# Configuration

Configure defaults for dev environments.

See [api-svc/.../environment/readme.md](../../api-svc/spring/src/main/java/raido/apisvc/spring/config/environment/readme.md)


# Logging

* Avoid string concatenation before the level is checked - use structured 
  logging api
* Are appropriate log levels used?
  * remember that `debug` is off by default in prod deployments
  * use debug for debug/developer stuff
  * limit the amount of info and above calls, consider how the info will be 
  used in prod

See [api-svc/doc/logging.md](../../api-svc/doc/logging.md)