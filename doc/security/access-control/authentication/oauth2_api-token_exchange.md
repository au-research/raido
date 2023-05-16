
This documents a "happy day" sign-in process using Google as the ID Provider.

Assume:
* the user is already signed-in to google
* the app-user record
  * exists in DB because it was already approved by an OPERATOR or SP_ADMIN
  * is enabled and otherwise valid

Note:
* I have used Mermaid optional boxes (`opt`) to try to highlight and separate 
  the OAuth2 part of the process from the custom Raido part where we generate 
  our own api-token for use with API endpoint calls.  

```mermaid
sequenceDiagram
autonumber
actor user as User<br/>(web browser)
participant app as app-client<br/>(SPA served from<br/> AWS CloudFront) 
participant client as api-svc<br/>(container hosted on<br/>AWS ECS)
participant idp as Google<br/>(google.com,<br/>googleapis.com, etc.)

user->>app: user navigates to <br/>app.prod.raid.org.au
user->>app: user clicks<br/>`Sign in with Google`
app-->>user: App sets location to <br/>accounts.google.com<br/>/o/oauth2/v2/auth
note left of app: scope: openid, email, profile<br/>state: {client_redirect_uri, client_id}


opt OAuth2 / OIDC
user-->>idp: browser follows location navigation
idp->>idp: 
note right of idp: user signs in to Google<br/>if not already
user->>idp: user consents to "authorize app"
idp-->>user: 302 redirect to redirect_uri<br/>(api.prod.raid.org.au/idpresponse)
note left of idp: {code, state}
user-->>client: browser follows redirect to api-svc<br/>(api.prod.raid.org.au/idpresponse)

client-->>client: 
note right of client: parse state param and<br/>match google client_id

client->>idp: GET auth2.googleapis.com/token
note right of client: {grant_type: authorization_code,<br/>code, client_id, client_secret}
idp->>client: 
note left of idp: {id_token}

client-->>client:  
note right of client: verify id_token RS256 JWT signature<br/>using certificate from<br/>www.googleapis.com/oauth2/v3/certs
end


opt Raido custom api-token generation
client-->>client:  
note right of client: verify app-user exists in DB<br/>and is enabled   

client-->>client:  
note right of client: create api-token, HS256 JWT<br/>signed with non-shared api-svc secret   

client-->>user: 302 redirect to client_redirect_uri<br/>(app.prod.raid.org.au)
note left of client: {api-token}
end

user-->>app: browser follows redirect to app-client
app-->>app: 
note right of app: parse api-token from url and store to<br/>be sent with all authorised requests

```

* (3) `SignInContainer.tsx googleSignIn()`
* (9) `AppUserAuthnEndpoint.java authenticate()`
* (17) `AuthProvider.tsx checkLoginState`