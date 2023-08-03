# Load test pre-requisites

## System under test

The load-test must be configured to point at the system under test, for example:
by setting the sys-prop 
`-DGatlingRaidoServerConfig.apiSvcUrl=https://api.demo.raid.org.au`.


## Authentication

The load-test must be configured with the `apiKeyJwtSecret` so that we can 
bootstrap an api-token that we can then use to generate service-points and 
api-keys for the load test, for example:
`-DGatlingRaidoServerConfig.apiKeyJwtSecret=$API_KEY_JWT_SECRETS`.

The load-tests use this secret to prepare and sign a fake "boostrap" api-token 
for an app-user associated with the raido service-point with an OPERATOR role.

The [PrepareServicePoints](/src/gatling/java/raido/loadtest/simulation/PrepareServicePoints.java)
simulation uses the bootstrapped api-token to set-up the service-points and 
api-keys that the test will use.


## Dependent systems

Read the [load-testing-prerequisites.md](./doc/load-testing-prerequisites.md)
before running a load-test, even locally.

Generally, we "stub out" the dependent systems, so that we don't generate load
on those systems (at least orcid and doi will actually start to rate limit us
by IP addresses and that will caused the tests to fail.

The SUT needs to be configured to use the "in-memory" stubs to avoid generating
load on dependent systems


# Scenarios

## Warm up

Most simulations start with a `warmUp` scenario that tries to confirm the 
system is running and the load test can reach it by querying the 
`/public/status` url.

If the status url query fails, then likely the service is not up or unreachable,
the symptom is probably an error looking something like: 
`session not found url=/public/status`.


# Troubleshooting

## Diagnosis

Looks for errors in the "set up" scenarios that create  service-points, 
api-keys, api-tokens and other pre-requisites for the test itself.  
Hopefully the test doesn't even run if these fail, but it's easy to get that 
wrong with Gatling and the tool will try to do the load testing anyway.

## Connectivity
* Is the service running? 
  * (I often forget this when doing local dev), is your
  config correct and pointing at the service it should be?
* Does the place you're running the test have a working connection to the
service?
  * i.e. are there network rules/connectivity getting in the way?


# Load test results

See [load-test/doc/log](./doc/log) for a log of load-test runs and their results