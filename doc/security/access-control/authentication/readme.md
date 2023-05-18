
Raido uses the OAuth2 and OIDC standards to authenticate/identify a human 
user of the system.
We then exchange the standard OIDC id_token for our own api-token, which
is just a simple
[JWT bearer token scheme](https://oauth.net/2/bearer-tokens/),
not related to the more advanced OAuth concepts of authorization delegation,
etc. 

As far as possible, we've tried to not use any the standard OAuth terms for the
Raido authorization components.

[oauth2_api-token_exchange.md](../authentication/oauth2_api-token_exchange.md)
shows how OAuth2 / OIDC flow works, and how we then exchange the OAuth2
id_token for the Raido concept of an api-token.


# Terminology

## OAuth2/OIDC terms
* id_token
  * This is the element of the OAuth/OIDC scheme that Raido cares about, in 
    order to authenticate the identity of the user.
* access_token
  * This is the element of the OAuth scheme that would allow us (Raido) to 
    do things on the human users behalf in the relevant system (Google, 
    ORCID, etc.)  
  * This is the part that we **don't** currently use.  We don't do anything 
    with or read extra info from your Google or other accounts.  The sole 
    purpose of the Raido authentication scheme is just to let users sign-in 
    without having to create an account or new password with Raido.
  * This might change in future - many stakeholders have offhandedly 
    mentioned doing un-specified "stuff" with ORCID records and RAiD 
    metadata but this is currently not a focus of development. 
* client_id
  * Before Raido can do any OAuth/OIDC stuff, the ARDC has to have an 
    agreement with the ID Provider to provide authentication services.
    The client_id is a technical representation of this agreement.
* client_secret
  * This is a secret known only to the IDProvider and the "OAuth client",
    AKA "Relying party", it's an internal technical detail of the grant flow
    type what we use.
* redirect_uri
  * another technical and security mechanism, this helps associate the 
    authentication process with the website/app/service.  This must also 
    be pre-agreed with the IdProvider and only this specific URI (there 
    can be multiple though) can be used for authentication purposes, it controls
    where the IdProvider will send the user's browser after authentication 
    succeeds.
  

## Raido terms
* service-point
* api-key
* api-token
* app-user
* role

See [authorization](../authorization) for details about these terms and how 
the Raido authorization scheme works.
