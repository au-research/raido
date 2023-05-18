# Unapproved user sign-in flow 

This diagram shows the flow of requests for a new unapproved user signing in 
via Google and requesting access to Raid, including specifying what 
service-point they're associated with.

See [oauth2_api-token_exchange.md](../authentication/oauth2_api-token_exchange.md)
for details about how the OAuth2 / OIDC flow works.

In the below a "NonAuthzToken" might also be though of as an "UnapprovedToken", 
it identifies a person who has not been approved to use the system.

```mermaid
sequenceDiagram
autonumber
actor user as Anonymous User<br/>(web browser)
participant app as app-client<br/>(SPA served from<br/> AWS CloudFront) 
participant api as api-svc<br/>(container hosted on<br/>AWS ECS)
participant idp as Google<br/>(google.com,<br/>googleapis.com, etc.)

user->>app: user navigates to <br/>app.prod.raid.org.au
user->>app: user clicks<br/>`Sign in with Google`
app-->>user: App sets location to <br/>accounts.google.com

opt OAuth2 / OIDC
  user-->>idp: browser follows location navigation
    Note over api, idp: OAuth2 / OIDC flow<br/>See oauth2_api-token_exchange.md
  idp-->>api: api-svc obtains<br/>verified OIDC id_token
end

opt Raido custom api-token generation
  api-->>api: _
    note right of api: no app-user exists in DB for<br/>the identity in id_token

  api-->>api: _
    note right of api: create "unapproved-api-token"<br/>IDP identity is confirmed but person<br/>is not authorized to use the system

  api-->>user: 302 redirect to client_redirect_uri<br/>(app.prod.raid.org.au)
  note left of api: {api-token:unapproved-api-token}

end

user-->>app: browser follows redirect to app-client
app-->>app: _
  note right of app: parse api-token from url

app-->>app: _
  note right of app: is api-token for unapproved identity?

app->>user: show "NotAuthorizedContent"

user->>app: select service-point and<br/>submit authz-request

app->>api: POST /request-authz/v1

api-->>api: _
  note right of api: store in table user_authz_request

api->>app: {status: REQUESTED}

app->>user: show confirmation

note right of user: user stays on this page, nothing else they<br/>can do because they are "unapproved"
```

* (6?) `AppUserAuthnEndpoint.java authenticate()`
* (12?) `NotAuthorizedContent.tsx` Not modeled as a stand-along "page" (you 
  can't navigate to to it or anything)
  * it's just a component that the `AuthProvider` shows when this state is 
    detected

---

# Admin approval flow

This diagram shows the flow of requests involved when an administrative type
user (OPERATOR or SP_ADMIN) approves a user for using Raid at a service-point.

For this diagram, I'm only going to show the API calls, rather than drawing
out the app-client stuff that doesn't really add anything to the
understanding.  These API calls could easily be made by integrating a
customer's systems to allow approving from withing their own API client
application.

Note that all these API calls are fully secured and calls must be
accompanied by an api-token generated for a user with the suitable role.

After this process ends, the app-user is considered "approved" to use the RAiD
application in the context of the requested service-point.

The original requesting user just signs in again and
they will follow the normal sign-in scenario - an api-token will be
generated and returned to the app-client and the user is now "signed in".

See [oauth2_api-token_exchange.md](../authentication/oauth2_api-token_exchange.md)
for details about how the standard OAuth2 / OIDC flow works.


```mermaid
sequenceDiagram
autonumber
actor user as Administrative User<br/>(OPERATOR / SP_ADMIN)
participant api as api-svc<br/>(container hosted on<br/>AWS ECS)

user->>api: GET /admin-authz-request/v1
api->>user: AuthzRequestExtraV1[]
user-->>user: user selects a<br/>specific AuthzRequest

user->>api: GET /request-authz/v1/{authzRequestId}
api->>user: AuthzRequestExtraV1

user-->>user: user indicates APPROVE
user->>api: POST /update-authz-request-status/v1<br/>{status: APPROVED}
api-->>api: _
note right of api:  insert record into app_user table<br/>with the identity in the authz-request

api->>user: 200


```

