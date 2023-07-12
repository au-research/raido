
# Introduction

The `api-svc` is a Spring application server that primarily serves the 
"V2" API: see [/api-svc/idl-raid-v2](/api-svc/idl-raid-v2).


# Architecture and deployment

The Raido api-svc is designed to operate on a "zero-downtime" basis.

That doesn't mean it will never be down (we support Raido on a "best effort",
in-hours basis), but the standard mode of operation and upgrade is designed
for zero downtime.

# Upgrading the api-svc

api-svc is intended to be completely stateless, running several compute nodes 
spread across separate physical datacenters.  New versions are rolled out
by adding nodes with the new version to the pool, and then draining the old
instances so that all versions are running the new software.

# Upgrading the database schema 

Schema migrations are carried out via Flyway, keeping backwards compatibility
between the new schema and the previous version of the api-svc so the two can
be deployed independently without either the api-svc or DB having to be taken 
offline.

# Upgrading / patching compute instances

Patching the compute instances is carried out the same way as upgrading the 
api-svc: new nodes are rolled across the pool until all instances are upgraded.

# Upgrading the Database instance

Raido uses AWS RDS to manage the Postgres database in a multi-az configuration.

When running minor database updates against this RDS configuration, AWS will
keep the database available the same "rolling upgrade" process as the api-svc.

When running major database updates, there is usually some downtime - on the 
order of 10 minutes.  Downtime like this is scheduled to happen in the 
AWS RDS maintenance window: 
[/doc/service-level-guide.md](/doc/service-level-guide.md) 


# Running for local development

* setup the postgres DB as described in [db/readme.md](./db/readme.md)
* create the database schemas
  * in the root directory of the git repo 
  * run `./gradlew flywayMigrate`
    * this command will create the schema for two schemata: 
    `api_svc` and `raid_v1_import`
    * the schema creation will also have created a user named `api_user`, using 
    the password being set from the value you previously configured in 
    `api-svc-db.gradle` as `apiSvcRolePassword`
  * run `./gradlew flywayInfo` to see the results of the migration tasks
* see [spring/readme.md](./spring/readme.md) for instructions on running the
  actual server, including configuring the password for the `api_user`

# Signing in to the app when doing local dev

In usual operation, someone needs to "authorise" you to use the app:
* you sign-in via an IDP (Google, etc)
* that takes you to a page where you request "authorisation" to specific 
  Service Point
* someone else, who is an Operator or SpAdmin "approves" you to use Raido with 
  your requested service point

To "bootstrap" the sign-in process, you need to sign in as an "auto-approved" 
operator, using the Google IDP and requesting authorisation to the `Raido` 
service point.

The list of "auto-approved" users is stored in the `raido_operator` table:
* rows are inserted for all environments (including PROD) via standard flyway
  migration (e.g. [V4__sign_in_tables.sql](./db/raido/src/main/resources/db/migration/V4__sign_in_tables.sql) 
* DEMO environment: [V8_1__DEMO_surf_operators.sql](./db/raido/src/main/resources/db/env/demo/V8_1__DEMO_surf_operators.sql)

For new devs, add a file like `env/demo/V16_2__DEMO_first_last.sql`: 
```
insert into raido_operator
values ('first.last@ardc.edu.au')
on conflict do nothing;
```

For the flyway version number, set it based off whatever the main flyway 
migration is up to or you'll get an "out of order" execution error.  

If you've done it right; when you sign-in to Google and then create an 
authz-request using that email address - you should get a browser alert dialog 
saying that you've been auto-approved after hitting the submit button.


# Importing legacy data for use in local environment
This is optional (and the data files are not publicly available) - it's not 
necessary for just running Raido. 

Import the raid V1 data into the `raid_v1_import` schema:
  * see [v1-ddb-migration/readme.md](./db/v1-ddb-migration/readme.md)

