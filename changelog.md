# Audience

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


# Not released

_Not yet scheduled_

## App-client UI
* Spatial coverage field
* Traditional knowledge label field

## API 
* Validation to check ORCID exists for contributor
* Validation to check ROR exists for organisation
* New metadata blocks:
  * spatial coverages
  * traditional knowledge labels

## Infrastructure


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


