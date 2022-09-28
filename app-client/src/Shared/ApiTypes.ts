
/** Specifies he shape of our claims in the AccessToken. */
export interface AuthzTokenPayload {
  clientId: string,
  servicePointId: number,
  sub: string,
  email: string,
  /* role in access token is for client's convenience, actual authz checks 
  should be done against the DB, not the claim.
  Not even using it at the moment. */
  role: string,
}


/** We need a client-driven redirectUri so we can use the same endpoint for 
different clients (think: localhost for dev, DEMO and PROD environments. 
Can avoid this by using multiple authn APIs, one per environment (e.g. similar
to what Github does where you can only have one /idpresponse callback URL 
per client).
We send the clientId so that we can tell what IDP the user authenticated with,
we assume that the clientId would be unique across different IDPs, and obviously
they are unique to the same IDP (ie. two Google OAuth clients have different
clientId values).  We need it in the state because some IDPs don't send the 
clientId param to the server. 
 */
export interface RaidoOAuthState {
  redirectUri: string,
  clientId: string,
}

