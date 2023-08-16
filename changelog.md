See the [Changelog audience](#changelog-audience) section for info about 
 the expected audience and content of the changelog.

# Next version

_Not yet scheduled_

## App-client UI
* none
 
## API
* updates to experimental API to align terminology
  * unapproved-user-experimental.yaml renamed from "unauthz"

## Infrastructure
* AgencyPublicDataExport - see [/doc/data-export](/doc/data-export).
* moved docker build project from api-svc/docker to api-svc/docker/ecs
** DEMO env is updated and deployed, but PROD infra must be updated

---
# 1.2.1
* Fix to validation of ORCID to allow x in checksum
---

# 1.2

_2023-05-15_

## App-client UI
* Additions to mint/edit page:
  * Spatial coverage field
  * Traditional knowledge label field
* Option to disabled editing by service point

## API 
* PID validation
  * check ORCID exists for contributor
  * check ROR exists for organisation
  * check DOI exists for service point
  * note that the service-level-guide has been updated to allow that API 
    requests maybe rejected if they have too many PIDs associated
* New metadata blocks:
  * spatial coverages
  * traditional knowledge labels

## Infrastructure
* introduced the InMemory stubs for APIDS, ORCID, ROR and DOI
  * this is noted in changelog not because it changes anything, but the 
    deployment plan should take it into account, specifically: 
    * attention should be paid to env config (shouldn't need changing)
    * post-deploy tests should verify that we didn't accidentally start using 
      in-memory handles in production
* DB changes 
  * bumped width of `raid.handle` column
  * edit service point new column
* Metrics publishing to AWS CloudWatch introduced
  * must be enabled in config
  * remember to add the permissions to the ECS task role (not the ASG 
    cluster role), see DEMO `ApiSvcEcs`

---

# 1.1

_2023-04-11_

## App-client UI changes 

* Change public landing page title from "Raido" to "RAiD"
* Add sign-in warning of uBlock Origin / ORCID issue
* Add "Copy handle" button to the home page list
* Add download for "recently minted raids" report
* Default the lead organisation on the mint page to the institution
  associated with the service-point.
* Add item to menu for admin users to access API keys

## API changes

New metadata blocks:
* subjects 
* related Raids
* related objects 
* alternate identifiers


# 1.0

_2023-03-15_

Initial production deployment.

---

# Changelog audience

The changelog has multiple audiences:
* communications team - for preparing the notification email that
  will be sent for a new deployment
  * mostly interested in the UI section
  * but they also need to know about deprecations of stable API endpoints
* client API integrators
  * mostly interested in the API section
* test team
  * interested in both UI And API sections
* deployment team
  * interested in the infrastructure section
