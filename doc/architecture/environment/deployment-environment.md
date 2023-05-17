
# Deployment Environment

We use the [AWS CDK](https://github.com/aws/aws-cdk) to build and deploy the
various parts of the stack.

https://github.com/au-research/raido-v2-aws-private/

The setup and configuration of your deployment process depends heavily on your
[operational environment](./operational-environment.md).

For example, our CloudFront is configured to serve the app-client from S3.
We have a AWS CodeBuild project that can be invoked to run the app-client
build process and deploy the built files to S3.  CloudFront serves those files
over the internet.

How you deploy those files will depend on your environment,
you might:
* configure your web-server to read files from a network drive (via NFS protocol
  or similar)
  * then your build process just needs to copy the files to the expected network
    location

* configure your web-server to read files from its local drive
  * then your build process would need to use `scp` or a similar protocol to 
  copy the files to each web server machine


# ARDC RAiD Service

## AWS infrastructure 

Our AWS infrastructure is deployed via an 
[AWS CDK pipeline](https://docs.aws.amazon.com/cdk/v2/guide/cdk_pipeline.html) 
in a "continuous integration" style  - i.e. AWS infrastructure is automatically 
updated when a change is pushed to the `/main` branch of the AWS repo.


## Database schema

The database instance itself is managed via CDK.
The database schema (tables, columns, triggers, etc.) is implemented as 
[Flyway](https://flywaydb.org/) migrations that are deployed by an 
[AWS CodeBuild](https://aws.amazon.com/codebuild/) task.

* [RaidoDbFlywayMigrate project](https://github.com/au-research/raido-v2-aws-private/blob/fd26c55ab476533e6c3d9c2cd6f712046b101ba1/raido-root/lib/prod/raido/RaidoDbCodeBuild.ts#L69)


## api-svc docker container 

The api-svc container instances are managed as [AWS ECS](https://aws.amazon.com/ecs/) 
tasks.

The docker container that contains the actual api-svc task is built by an 
AWS CodeBuild project: 
[ApiSvcPushDockerImage project](https://github.com/au-research/raido-v2-aws-private/blob/fd26c55ab476533e6c3d9c2cd6f712046b101ba1/raido-root/lib/prod/api-svc/ApisSvcCodeBuild.ts#LL50C53-L50C68)

The core Gradle task that initiates the Java compile and docker build (called 
by the PushDocker image project) is 
[ecsDockerPush](/api-svc/docker/build.gradle)


## app-client build

The app-client is built as a [create-react-app](https://create-react-app.dev/) 
project ([Webpack](https://webpack.js.org/) under the covers),
then deployed via a CodeBuild project:
[AppClientDeploy](https://github.com/au-research/raido-v2-aws-private/blob/fd26c55ab476533e6c3d9c2cd6f712046b101ba1/raido-root/lib/prod/app-client/AppClientCodeBuild.ts#L36)

The core build task is a simple `npm run build`, then the project copies
the built files to S3.


