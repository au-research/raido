
# Operational Environment 

To run the Raido codebase in a non-AWS environment, you would need at least the 
following.  

The following descriptions were mapped directly from the  
[raid-architecture.md](/doc/architecture/raid-architecture.md).


## DNS
* [AWS Route53](https://aws.amazon.com/route53/)
* https://github.com/au-research/raido-v2-aws-private/blob/main/raido-root/lib/prod/Route53.ts


## TLS certificates
* [AWS Certificate Manager](https://aws.amazon.com/certificate-manager/)
* https://github.com/au-research/raido-v2-aws-private/blob/main/raido-root/lib/prod/Us1Certificate.ts


## app-client - front-end web server

The app-client is a React "Single Page App", built as a purely static set of 
files to be loaded by the web browser.  It doesn't need much from the web
server beyond just serving the static files and taking care of TLS.

* [AWS CloudFront](https://docs.aws.amazon.com/cloudfront/index.html)
* https://github.com/au-research/raido-v2-aws-private/blob/main/raido-root/lib/prod/app-client/AppClientCloudFront.ts
* [architecture decision log](/app-client/doc/adr)


###  load balancing

Front-end Load balancing is completely encapsulated by the CloudFront service.
CloudFront handles caching of files local to the end-user (if configured) and 
handles all routing of user traffic.

We also have some rules set up so that the CloudFront instances forward API 
traffic to the api-svc load balancer.  This is not necessary (and incurs cost
at the Cloudfront/networking level), but it removes
the need for the browser to send pre-flight CORS requests.

See https://github.com/au-research/raido-v2-aws-private/blob/fd26c55ab476533e6c3d9c2cd6f712046b101ba1/raido-root/lib/prod/app-client/AppClientCloudFront.ts#L53


## api-svc - back-end API service

Raido is a Java/Spring API server.
It is stateless (we do not use session state) and designed for 
horizontal scalability. 

* [AWS ECS](https://aws.amazon.com/ecs/) running docker containers stored in 
  [AWS ECR](https://aws.amazon.com/ecr/)
* https://github.com/au-research/raido-v2-aws-private/blob/main/raido-root/lib/prod/api-svc/ApiSvcEcs.ts
* [Dockerfile](/api-svc/docker/src/main/docker/Ecs.Dockerfile)
* [architecture decision log](/api-svc/doc/adr/readme.md)


### load balancing

We use [AWS ALB](https://aws.amazon.com/elasticloadbalancing/application-load-balancer/) 
in combination with ECS for routing API traffic to api-svc containers running 
in ECS.

* https://github.com/au-research/raido-v2-aws-private/blob/main/raido-root/lib/prod/raido/ProdRaidOrgAuAlb.ts
* https://github.com/au-research/raido-v2-aws-private/blob/fd26c55ab476533e6c3d9c2cd6f712046b101ba1/raido-root/lib/prod/api-svc/ApiSvcEcs.ts#L244


## Local Handle Service

We currently use the ARDC APIDS service for minting handles.
It is operated in an "on-premises" fashion, on ARDC hardware and network 
services, by the ARDC DevOps team (separate from the Raid team).

* https://ardc.edu.au/services/ardc-identifier-services/ardc-handle-service/
* https://github.com/au-research/ANDS-PIDS-Service
* [ApidsService.java](/api-svc/spring/src/main/java/raido/apisvc/service/apids/ApidsService.java)
* There's no AWS CDK code for this since it's operated externally to AWS
  * the configuration is stored in the ECS task definition
  * https://github.com/au-research/raido-v2-aws-private/blob/fd26c55ab476533e6c3d9c2cd6f712046b101ba1/raido-root/lib/prod/api-svc/ApiSvcEcs.ts#LL179C31-L179C31


## Database

Postgres 15 database server operated by [AWS RDS](https://aws.amazon.com/rds/).

* https://github.com/au-research/raido-v2-aws-private/blob/main/raido-root/lib/prod/raido/RaidoDb.ts
* [architecture decision log](/api-svc/doc/adr/2022-07-21-database.md)
* [postgres-specific-features.md](/api-svc/doc/postgres-specific-features.md)


## Log management and observability

The ECS task definition is configured to send logs to 
[AWS Cloudwatch](https://aws.amazon.com/cloudwatch/).

* https://github.com/au-research/raido-v2-aws-private/blob/fd26c55ab476533e6c3d9c2cd6f712046b101ba1/raido-root/lib/prod/api-svc/ApiSvcEcs.ts#L127

### Metrics
* [AWS CloudWatch metrics](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/working_with_metrics.html)
* [observability.md](/doc/observability.md)


## Secrets management

* [AWS Secrets Manager](https://docs.aws.amazon.com/secretsmanager/index.html)
* https://github.com/au-research/raido-v2-aws-private/blob/main/raido-root/lib/prod/api-svc/ApiSvcSecret.ts
* https://github.com/au-research/raido-v2-aws-private/blob/main/raido-root/lib/prod/Secrets.ts

