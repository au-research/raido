# Load tests for the api-svc

## Warm up

Most simulations start with a `warmUp` scenario that tries to confirm the 
system is running and the load test can reach it by querying the 
`/public/status` url.

If the status url query fails, then likely the service is not up or unreachable,
the symptom is probably an error looking something like: 
`session not found url=/public/status`.


# Trouble shooting

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
