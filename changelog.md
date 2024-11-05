See the [Changelog audience](#changelog-audience) section for info about 
 the expected audience and content of the changelog.

# 2.1.3
## API
• Update Datacite mapping to set resource type of RAiDs to 'project'

# 2.1.2
## App-client UI
* Component updates and refactoring
* Test framework and dependency management (Playwright for e2e, Vitest for unit tests)
* UI/UX enhancements (especially nested components)
* General maintenance and cleanup

# 2.1.1
## App-client UI
* Updated UI components and logic, including new mappings and service point UI logic.
* Removed unused code, such as mappings, helper functions, and Keycloak functions.
* Added new features like async/await, Postman request collection, SP switcher, CORS to user-groups endpoint, and a restore button.
* Created and refined error handling, history pages, and mapping files.

## API
* Fixed bug that set embargoed Raids to 'draft' in Datacite. Embargoed now use the 'register' event.
* Fixed bug with missing Raid history.

## IAM
* Added endpoint to allow users to switch between active service points.
* Added endpoint to expose all groups a user belongs to.

## BFF
* Added BFF (Backend for Frontend) to store additional data for UI that we don't want in the API.

## Infrastructure
* Added `stage` environment

# 2.1.0
## App-client UI
* Added API validation error messages to the frontend
* Added service point selector
* Added additional end-to-end tests
* Removed unused legacy code
 
## IAM
* SAML authentication with AAF 

## Infrastructure
# 2.0.1

_2024-05-14_

## App-client UI
* Minor bug fixes

## API
* Bug fix to allow RAiDs to be findable in Datacite

---
# 2.0.0

_2024-04-22_

## App-client UI
* All new UI

## API
* Handles replaced with DOIs
* RAiD versioning and history
* Removal of auth endpoints from API
* Removal of experimental API

## Infrastructure
* Shared stack templates across environments

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
