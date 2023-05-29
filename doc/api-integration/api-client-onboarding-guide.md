

Intended to by a quick-start guide, focused on what a client needs to do to 
get up and running with the Raido API and minting raids.


# Business onboarding
* send an email to `contact@raid.org`
* you will be required to make an agreement with the ARDC about your usage 
  of RAiD
* as part of onboarding
  * we will create a service-point that identifies your usage of RAiD for  
    your institution

A description of the relationship between raids, service-points and api-keys
can be found in the [permission-model](./permission-model.md).


# Technical onboarding

## Concepts
* environments DEMO vs PROD
* experimental vs stable API
* service level guide
* open vs closed raid data
* future global raid.org infra
  * anonymous resolve of open raid data for any registration-agency
  * searching raids

## How to mint a raid in DEMO environment
* sign-in via your chosen ID Provider
  * Google, AAF or ORCID
* request authorization for a service-point
  * Raid team creates service-point as part of biz onboarding
  * but you should send an email to `contact@raid.org` or otherwise notify 
    us that you have submitted an authorization-request
  * add a comment that you would like admin access so that you can manage 
    api-keys and generate api-tokens
* we will let you know when you can sign-in
* sign-in using the same ID provider you used before
  * authorization is linked to your ID Provider
* create an api-key
* generating an api-token
  * the api-token is to be considered sensitive, non-public information
    * it must be kept secret and should never be accessible to end-users
      * that is, do not embed the api-token in front-end applications or web-sites
    * the api-token is the only thing necessary to use the API and can be used 
      to mint/edit raids and see closed raid data
  * save this somewhere safe, we do not store it in our system
    * but new ones can be generated at any time by just clicking the button 
      again
* listing recent raids
  * call list raid with your api-token, that's it
* mint raid
* read a raid

