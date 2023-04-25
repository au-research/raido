See the [Changelog audience](#changelog-audience) section for info about 
 the expected audience and content of the changelog.

# Not released

_Not yet scheduled_

## App-client UI
* Additions to mint/edit page:
  * Spatial coverage field
  * Traditional knowledge label field
* Option to disabled editing by service point

## API 
* Validation to check ORCID exists for contributor
* Validation to check ROR exists for organisation
* New metadata blocks:
  * spatial coverages
  * traditional knowledge labels

## Infrastructure
* introduced the InMemoryApidsServiceStub
  * this is noted in changelog not because it changes anything, but the 
    deployment plan should take it into account, specifically: 
    * attention should be paid to env config (shouldn't need changing)
    * post-deploy tests should verify that we didn't accidentally start using 
      in-memory handles in production

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
