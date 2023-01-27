Notes on when I loaded stuff into the demo env database.

## DDB export

First, export the files from DDB and put them in the location that was 
hardcoded in the import script.


## Configure SSH for tunnelling to the bastion 

`ssh` configured like this, `~/.ssh/.config`:
```
### Raido

Host sto-bastion.demo.raido-infra.com
  User ec2-user
  # don't forget to update the remote build.gradle task 
  IdentityFile ~/.config/raido/keypair/sto-bastion-keypair-2022-08-14.pem
  StrictHostKeyChecking=no

Host bastion
  HostName sto-bastion.demo.raido-infra.com
  User ec2-user
  IdentityFile ~/.config/raido/keypair/sto-bastion-keypair-2022-08-14.pem
  StrictHostKeyChecking=no

Host 10.20.*
  User ec2-user
  IdentityFile ~/.config/raido/keypair/api-svc-keypair-2022-08-17.pem
  StrictHostKeyChecking=no
  ProxyCommand ssh sto-bastion.demo.raido-infra.com nc %h %p

```


## Start local forwarding tunnel

Then started a local forwarding tunnel like this:
```
ssh  -o "StrictHostKeyChecking=no" -NL 127.0.0.1:2345:raido.cy2mh3kpqocs.ap-southeast-2.rds.amazonaws.com:5432 ec2-user@sto-bastion.demo.raido-infra.com
```
[https://explainshell.com/explain?cmd=ssh+-L+-N](https://explainshell.com/explain?cmd=ssh+-L+-N)

## Configure the import task connection details

Look up the connection deets, specifically password, from the 
[env.props](https://ap-southeast-2.console.aws.amazon.com/systems-manager/parameters/CFN-EnvProps5EA83047-9tMsJ4E4rnz1/description?region=ap-southeast-2&tab=Table).
and set them in: `api-svc-demo-migrate-db.gradle`:

```groovy
// this file used to run migration into the AWS demo environment database

// this is the local forwarding tunnel, see the /doc in the codebase
apiSvcPgUrl="jdbc:postgresql://localhost:2345/raido"

apiSvcPgUser="api_user"
apiSvcPgPassword="password"
```


## Invoke import task

```
./gradlew :api-svc:db:v1-ddb-migration:importS3Files -DRAIDOV2_APISVC_DB_CONFIG_PATH=c:/users/stolley/.config/raido/api-svc-demo-migrate-db.gradle
```