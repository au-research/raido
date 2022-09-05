
# The main stack

* Typescript
* Java 
* SQL 


# Glue code

Little bits of scripting stuff, the usual glue code.

* Docker  
  * i.e. `Dockerfile`
* Groovy
  * Gradle build scripts
  * legacy data migration
* Shell scripts
  * for booting up EC2 instances, 
  * `codebuild` definitions
* Scala 
  * load testing
  * doesn't exist yet, will use Gatling
* Javascript
  * little bits of glue, like in NPM scripts, etc.


# Infrastructure

AWS, CDK - TypeScript

For CI/CD, we use a lot of AWS: codebuild, codepipeline, lambda, ECR.

The AWS code is in a separate repo, there's no credentials in there but we're
still reluctant to make it publicly available, especially early during 
development.

https://github.com/au-research/raido-v2-aws-private

We would be open to working on a simple "example" AWS CDK infrastructure. 


# Database

## Postgres

Managed via AWS RDS. 

No cross-database support, we take full advantage of Postgres features without 
needing to worry about cross-DB compatibility

##  Flyway 

For Schema migration.

Developers are encouraged to understand SQL and use it to full potential, 
with jOOQ helping us take advantage of static typing for our queries.


# Backend

* Docker 
  * very simple setup running on EC2 via shell script, 
  see [/api-svc/docker](/api-svc/docker)
    * unfortunately, the actual shell script to install and run the container 
      is in the private rep 
* Java
  * JDK 17 - Corretto
* Spring
  * Not spring-boot


# Frontend

* Typescript
* React
  * With `react-query`, but no Redux or similar Flux implementation
* Material UI


# REST

We use OpenApi with an “API first” approach, Java and Typescript code is 
generated from the OpenApi definition.

* [/api-svc/idl-raid-v2](/api-svc/idl-raid-v2)
* [/app-client](/app-client)