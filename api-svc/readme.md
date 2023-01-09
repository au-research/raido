
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
  * run `./gradlew :api-svc:db:raido:flywayMigrate`
  * the migration will create the `api_user`, but the user is disabled 
because it has no password
* connect to your local database and run the following to set a password
  * `alter user api_user password ''` (supply your own password)
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
* DEMO environment: [V4_1__surf_operators.sql](./db/raido/src/main/resources/db/env/demo/V4_1__surf_operators.sql)

If you've done it right, you should get an alert dialog telling you you've been
auto-approved immediately after hitting the submit button.


# Importing legacy data for use in local environment
This is optional (and the data files are not publicly available) - it's not 
necessary for just running Raido. 

Import the raid V1 data
  * run `./gradlew :api-svc:db:v1-ddb-migration:flywayMigrate`
  * see [v1-ddb-migration/readme.md](./db/v1-ddb-migration/readme.md)

