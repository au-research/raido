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


# api-svc

## IDL projects generate java of the from `Void entpointNAme()`   

When we have a POST endpoint that doesn't return anything, we end with a method
with the `Void` return type, which we then have to `return null` from, ick.

This happens because we customise the return type of OpenAPI generated methods
to not wrap return values in a `ResponseEntity`.  
Returning `ResponseEntity<Void>` makes sense, returning `Void` is silly.
We should be generating methods that just return `void` in this case.

This might not be worth fixing though.  Most endpoints will end up returning a 
generic "sucess/fail" type that can return info about the failure. So the 
awkwardness with `Void` may not turn out to be much of an issue.

# AWS / Infra

## Github Personal Access Token must be rotated

The CDK pipeline uses a personal access token, created in a user's account
(STO).

If the token is not rotated before it expires (currently Aug 12 2023), we will
not be able to deploy AWS infra updates.  And there may be other implicit 
dependencies by then that we don't realise we have.

Not sure what the alternative is.  