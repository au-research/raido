Eventually, this directory will contain full explanations and diagrams of 
how all aspects of Raido security works:
* OIDC
* authorization tokens
  * an "authz token" is similar to an OIDC "access token" but it's specific 
    to Raido
  * the "access token" concept still exists in Raido, but the "access token" 
    and the "id token" are both thrown away after the user is authenticated 
    through the OIDC process
* app-user
* api-key
  * api token