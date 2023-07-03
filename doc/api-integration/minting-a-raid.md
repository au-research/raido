# Minting a raid in the DEMO environment

# Pre-requisites

As outlined in the [onboarding-guide](./api-client-onboarding-guide.md); 
before you can mint a raid, you must have an agreement with the ARDC and have 
had a service-point created for your usage.

See the [permission-model](./permission-model.md) to gain an understanding 
of service-points and other important concepts.


# Get your api-token

In order for your client to talk to the Raido API, you need an api-token.

## Create a user authz-request to use the system 
* sign-in via your chosen ID Provider
  * Google, AAF or ORCID
* submit the form to request authorization for a service-point
  * The Raid support team will have already created service-point as part of 
  [business onboarding](./api-client-onboarding-guide.md#business-onboarding)
  * It can help to send an email to `contact@raid.org` or otherwise notify
    us (via email, slack, GitHub discussions, etc.) that you have submitted an 
    authorization-request
  * add a comment to your authz-request that you would like admin access so 
  that you can manage api-keys and generate api-tokens
* the raid support team will contact you when you can sign-in


## Create an api-token

* sign-in using the same ID provider you used before
  * authorization is linked to your ID Provider
* use the menu to navigate to the api-keys list page and create an api-key
  * give your api-key a name that identifies your client application
* click the `Generate token` button to generate your api-token

### Example

For the examples outlined below, we're going to do basic `curl` commands to 
mint and read a raid.

To make these commands readable, we're going to store the api-token into an 
environment variable that will be used by the example commands:
```
export DEMO_TOKEN="xxx.yyy.zzz"
```

Note that we _don't_ prepend the `Bearer ` prefix, since we do that below in
the header specification of the example commands.


### api-token security

$${\color{red}**WARNING**}$$

The api-token is to be considered sensitive, non-public information.

api-tokens must be kept secret and should never be accessible to 
end-users
  * that is, do not embed the api-token in front-end client applications or 
  web-sites
  * the api-token is the only thing necessary to use the API and can be used
    to mint/edit raids and see closed raid data
  * save the api-token somewhere safe - we do not store it in our system
    * but note that new api-tokens can be generated at any time by just 
    clicking the generate button again


## Mint a raid
* use the stable "mint" endpoint 
([`/raid/v1`](/api-svc/idl-raid-v2/src/raido-openapi-3.0.yaml))
to create a raid
* you must set your api-token in the `Authorization` header (don't forget to 
prefix with `Bearer ` in the value)
* use the OpenAPI definitions as a guide to what fields are required
  * the metadata schema guide to the intended content of the fields is 
  only currently available as a document available by emailing to 
  `contact@raid.org`
  * eventually, this document will be available as a public 
  "[read-the-docs](https://readthedocs.org/)" website

### Example
```
curl -v -X POST https://api.demo.raid.org.au/raid/v1 \
  -H "Authorization: Bearer $DEMO_TOKEN" \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
  "metadataSchema": "RaidoMetadataSchemaV1",
  "titles": [
    {
      "title": "Client Integration Test RAID No. 1",
      "type": "Primary Title",
      "startDate": "2023-02-01"
    }
  ],
  "dates": {
    "startDate": "2023-02-01"
  },
  "descriptions": [
    {
      "description": "Test Description",
      "type": "Primary Description"
    }
  ],
  "access": {
    "type": "Open"
  },
  "contributors": [
    {
      "id": "https://orcid.org/0000-0002-6492-9025",
      "identifierSchemeUri": "https://orcid.org/",
      "positions": [
        {
          "positionSchemaUri": "https://raid.org/",
          "position": "Leader",
          "startDate": "2023-02-01"
        }
      ],
      "roles": [
        {
          "roleSchemeUri": "https://credit.niso.org/",
          "role": "supervision"
        }
      ]
    }
  ]
}'
```

## Read a raid
 
* use the stable "read" endpoint 
([`/raid/v1/{prefix}/{suffix}`](/api-svc/idl-raid-v2/src/raido-openapi-3.0.yaml)) 
to read the minted raid

### Example
```
curl -v https://api.demo.raid.org.au/raid/v1/10378.1/1709242 \
  -H "Authorization: Bearer $DEMO_TOKEN" \
  -H 'Accept: application/json' 
```
