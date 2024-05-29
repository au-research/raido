
# Operational Environment 

To run the Raido codebase in a non-AWS environment, you would need to consider 
how to provide equivalent services for each of the sections outlined below. 

The below sections were mapped directly from the  
[raid-architecture.md](/doc/architecture/raid-architecture.md).


# ARDC RAiD Service environment in AWS

## DNS
* [AWS Route53](https://aws.amazon.com/route53/)
* [https://github.com/au-research/raido-v2-aws-private/blob/main/raido-root/lib/prod/Route53.ts](https://github.com/au-research/raido-v2-aws-private/tree/main/lib/raid/construct/route53)


## TLS certificates
* [AWS Certificate Manager](https://aws.amazon.com/certificate-manager/)
* [https://github.com/au-research/raido-v2-aws-private/blob/main/raido-root/lib/prod/Us1Certificate.ts](https://github.com/au-research/raido-v2-aws-private/blob/main/lib/raid/stack/RaidEnvironmentStage.ts#L35)
* [https://github.com/au-research/raido-v2-aws-private/blob/main/lib/raid/construct/route53/RaidPublicHostedZone.ts#L18
](https://github.com/au-research/raido-v2-aws-private/blob/main/lib/raid/construct/route53/RaidPublicHostedZone.ts#L18)

## External sign-in services

Raido uses [Single sign-on](https://en.wikipedia.org/wiki/Single_sign-on).
Raido is designed to use identity services OAuth2 / OIDC to allow users to 
sign-in with pre-existing credentials.  
Note that Raido currently implements no direct sign-in method 
(i.e. storing username/email/password data in our DB).

Currently, we authenticate uses with the following providers:
* [Google](https://developers.google.com/identity/openid-connect/openid-connect)
* [AAF](https://support.aaf.edu.au/support/solutions/articles/19000096640-openid-connect-)
* [ORCID](https://info.orcid.org/documentation/features/public-api/orcid-as-a-sign-in-option-to-your-system/)

Each of these authentication methods requires an agreement between the Relying 
Party (i.e. ARDC) and the Identity Provider.

See [/doc/security/access-control/authentication](/doc/security/access-control/authentication)
for more detail about how sign-in and acess-control works.


## app-client - front-end web server

The app-client is a React "Single Page App", built as a purely static set of 
files to be loaded by the web browser.  It doesn't need much from the web
server beyond just serving the static files and taking care of TLS.

* [AWS CloudFront](https://docs.aws.amazon.com/cloudfront/index.html)
* [https://github.com/au-research/raido-v2-aws-private/blob/main/raido-root/lib/prod/app-client/AppClientCloudFront.ts](https://github.com/au-research/raido-v2-aws-private/tree/main/lib/raid/construct/cloudfront)


###  Load balancing

Front-end Load balancing is completely encapsulated by the CloudFront service.
CloudFront handles caching of files local to the end-user (if configured) and 
handles all routing of user traffic.

We also have some rules set up so that the CloudFront instances forward API 
traffic to the api-svc load balancer.  This is not necessary (and incurs cost
at the Cloudfront/networking level), but it removes
the need for the browser to send pre-flight CORS requests.

See [https://github.com/au-research/raido-v2-aws-private/blob/fd26c55ab476533e6c3d9c2cd6f712046b101ba1/raido-root/lib/prod/app-client/AppClientCloudFront.ts#L53](https://github.com/au-research/raido-v2-aws-private/blob/main/lib/raid/construct/cloudfront/RaidUiCloudFront.ts#L74)


## api-svc - back-end API service

Raido is a Java/Spring API server.
It is completely stateless, designed for horizontal scalability.
* no 2nd level caching or similar
* no use of HTTP session state
* load balancing can be completely "un-sticky"

* [AWS ECS](https://aws.amazon.com/ecs/) running docker containers stored in 
  [AWS ECR](https://aws.amazon.com/ecr/)
* [https://github.com/au-research/raido-v2-aws-private/blob/main/raido-root/lib/prod/api-svc/ApiSvcEcs.ts](https://github.com/au-research/raido-v2-aws-private/tree/main/lib/raid/construct/ecs)
* [Dockerfile](/api-svc/docker/src/main/docker/Ecs.Dockerfile)
* [architecture decision log](/api-svc/doc/adr/readme.md)


### Load balancing

We use [AWS ALB](https://aws.amazon.com/elasticloadbalancing/application-load-balancer/) 
in combination with ECS for routing API traffic to api-svc containers running 
in ECS.

* [https://github.com/au-research/raido-v2-aws-private/blob/main/raido-root/lib/prod/raido/ProdRaidOrgAuAlb.ts](https://github.com/au-research/raido-v2-aws-private/blob/main/lib/raid/construct/loadbalancing/RaidApplicationLoadBalancer.ts)
* [https://github.com/au-research/raido-v2-aws-private/blob/fd26c55ab476533e6c3d9c2cd6f712046b101ba1/raido-root/lib/prod/api-svc/ApiSvcEcs.ts#L244
](https://github.com/au-research/raido-v2-aws-private/blob/main/lib/raid/construct/loadbalancing/ApiApplicationTargetGroup.ts)

### External PID services

Currently, the api-svc validates PIDs (ORCID, ROR, DOI) with external services, 
but it does so using only publicly available HTTP endpoints that do not require
authentication.

There are currently no API keys or other accounts/credentials that need to be
set up to allow this.  

Though the api-svc does requires network access to the internet so that 
http/https requests can be made.


## Local Handle Service

We currently use the ARDC APIDS service for minting handles.
It is operated in an "on-premises" fashion, on ARDC hardware and network 
services, by the ARDC DevOps team (separate from the Raid team).

* https://ardc.edu.au/services/ardc-identifier-services/ardc-handle-service/
* https://github.com/au-research/ANDS-PIDS-Service
* [ApidsService.java](/api-svc/spring/src/main/java/raido/apisvc/service/apids/ApidsService.java)
* There's no AWS CDK code for this since it's operated externally to AWS
  * the configuration is stored in the project configuration
  * [https://github.com/au-research/raido-v2-aws-private/blob/fd26c55ab476533e6c3d9c2cd6f712046b101ba1/raido-root/lib/prod/api-svc/ApiSvcEcs.ts#LL179C31-L179C31](https://github.com/au-research/raido-v2-aws-private/blob/main/config/EnvironmentProperties.ts#L58)


## Database

Postgres 15 database server operated by [AWS RDS](https://aws.amazon.com/rds/).

* [https://github.com/au-research/raido-v2-aws-private/blob/main/raido-root/lib/prod/raido/RaidoDb.ts](https://github.com/au-research/raido-v2-aws-private/blob/main/lib/raid/construct/rds/RaidRds.ts)
* [architecture decision log](/api-svc/doc/adr/2022-07-21-database.md)
* [postgres-specific-features.md](/api-svc/doc/postgres-specific-features.md)


## Log management and observability

The ECS task definition is configured to send logs to 
[AWS Cloudwatch](https://aws.amazon.com/cloudwatch/).

* [https://github.com/au-research/raido-v2-aws-private/blob/fd26c55ab476533e6c3d9c2cd6f712046b101ba1/raido-root/lib/prod/api-svc/ApiSvcEcs.ts#L127](https://github.com/au-research/raido-v2-aws-private/blob/main/lib/raid/construct/ecs/RaidEc2Service.ts#L44)

### Metrics
* [AWS CloudWatch metrics](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/working_with_metrics.html)
* [observability.md](/doc/observability.md)


## Secrets management

* [AWS Secrets Manager](https://docs.aws.amazon.com/secretsmanager/index.html)
* [https://github.com/au-research/raido-v2-aws-private/blob/main/raido-root/lib/prod/api-svc/ApiSvcSecret.ts](https://github.com/au-research/raido-v2-aws-private/blob/main/lib/raid/construct/secrets-manager/RaidSecrets.ts)https://github.com/au-research/raido-v2-aws-private/blob/main/lib/raid/construct/secrets-manager/RaidSecrets.ts
* https://github.com/au-research/raido-v2-aws-private/blob/main/raido-root/lib/prod/Secrets.ts

