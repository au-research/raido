# Dependent systems integration

Generally, we should avoid putting load on other people's systems without
coordinating ahead of time.

Thus, most load-tests should be using the appropriate
[stub functionality](../../spring/src/main/java/raido/apisvc/service/stub/readme.md)


# Load test notification requirements

## AWS

This is only necessary if running a high-load scenario against an AWS 
environment like DEMO or TEST.

https://aws.amazon.com/ec2/testing/
"Simulated Event Submissions Form": https://console.aws.amazon.com/support/contacts#/simulated-events


## External systems

If you are going to run a load-test against an external system, you should 
notify that organisation before doing it - it's just polite.  They also will 
likely tell you that your testing is invalid for reasons X, Y and Z - you 
should listen to them.
