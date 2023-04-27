
# api-svc

## Log aggregation

The ARDC RAiD service uses AWS CloudWatch, Metrics and log insights.


Raido has a specific logging strategy to keep our logs manageable and readable,
see [logging.md](../api-svc/doc/logging.md).


## Configuring metrics

See [MetricProps](../api-svc/spring/src/main/java/raido/apisvc/spring/config/environment/MetricProps.java)


## Publishing metrics

We use [Micrometer](https://micrometer.io/) as an abstraction layer over 
various metric implementations.

* JMX for local-dev: [metrics-jmx.md](../api-svc/doc/metrics-jmx.md) 
* CloudWatch for AWS environments: 
  [metrics-cloudwatch.md](../api-svc/doc/metrics-cloudwatch.md)
* see [metrics-naming.md](../api-svc/doc/metrics-naming.md) for an overview 
  of metric naming and namespaces
* Micrometer supports a bunch of other metrics tools if that's your jam:
  


# app-client

There's no centralised observability for the app-client at the moment.

The only way to debug an app-client issue is to be on the user's machine
and view the developer console.

Eventually, we will implement some kind of client-side error reporting, 
probably some kind of home-grown solution that logs to cloudwatch - like
BugSnag or Sentry, etc. but trading off cost for usability.

