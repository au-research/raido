CloudWatch metric publishing is only intended for use in higher environments
like DEMO and PROD (i.e. usually no used from local-dev or CI, etc.)

See [MetricProps](../spring/src/main/java/raido/apisvc/spring/config/environment/MetricProps.java)


## Publishing metrics from local-dev to AWS

Set the following:
```
# enable
MetricRegistry.awsEnabled=true

# this needs to be configured in your ~/.aws/config and profile
MetricRegistry.awsProfile=raido-demo

# not necessary, but it's basic courtesy - so we can tell which metrics are
# from your environment
EnvironmentConfig.envName=XXX
```

## Finding the metrics for your environment in the AWS console

* Open CloudWatch metrics console 
  * https://ap-southeast-2.console.aws.amazon.com/cloudwatch/home?region=ap-southeast-2#metricsV2:graph=~(timezone~'UTC)
* Look in `Custom namespaces`
  * look for `api-svc-<env name>`
    * where `env name` is driven by the `EnvironmentConfig.envName` property.
      * default value is "unknown", but please don't clutter up the Metrics 
        console with a bunch of `unknown' env metrics all being written to 
        in parallel by a bunch of unrelated local-dev environments
* See [metrics-naming.md](./metrics-naming.md) for details on individual metrics




