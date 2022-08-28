import { z as zod } from "zod";

/**
 note that bearer tokens (`id_Token` and `accessToken`) are transferred 
 "out of band" in the auth header and handled on both client and server by 
 infrastructure code rather than the application/business logic.
 `accessToken` is modelled here as optional so that:
 - client code can call it without passing the token and the infrastructure
   will take care of putting the token into the auth header 
 - server code can read the token from the auth header and bind it in when
   calling the implementation function so we have a standard place for it to be
   passed to the implementation functions.  
*/
export type AuthorizedPost<TReqest, TResult> =
  (req: TReqest, accessToken?: string) => Promise<TResult>;

//export type PostEndpoints<TReqest, TResult> = {
//  [name: string]:AuthorizedPost<TReqest, TResult>;
//}


/**
 * Endpoints related to authenticating and authorizing.
 * The have non-standard shapes and are dealt with by custom code.
 * Only infrastructure code needs to use these.
 */
export type AuthApi = {
  /**
   * Authorize the user's access to the app based on idToken and
   * issue an accessToken that must be passed when making access-restricted
   * API calls.
   */
  authorize: (idToken: string) => Promise<AuthorizeUserResponse>,
  /**
   * Used to bootstrap the client app, initially for config needed to
   * authenticate against the ID-Provider (Cognito).
   */
  readConfig: () => Promise<ServerInfo>,
}


/**
 * The token will conform to standard JWT claims (`exp`, etc.) and will 
 * also contain claims defined in AuthzTokenPayload.
 */
export type AuthorizeUserResponse = {
  succeeded: true,
  accessToken: string,
} | {
  succeeded: false,
  message: string,
}

/** Specifies he shape of our claims in the AccessToken. */
export interface AuthzTokenPayload {
  clientId: string,
  sub: string,
  email: string,
  /* role in access token is for client's convenience, actual authz checks 
  should be done against the DB, not the claim.
  Not even using it at the moment. */
  role: string,
}


/** requested by the client app so it knows how to authenticate, etc. */
export interface ServerInfo {
  directAuthn: {
    google: {
      issuer: string,
      clientId: string,
    },
    aaf: {
      issuer: string,
      clientId: string,
    },
  }
}

/* These "config" types model the server-side config that needs to be known by 
the client.  It' a subset of the information used to configure the server-side. 
The server-side code must be careful to only pass newly constructed objects to
pass back to the client. This is to avoid accidentally passing server-side data 
to clients. */

/** This is the stuff the client needs to know to do direct authentication;
 client must not see the secret, and doesn't care about the
 allowedCallbackUrls.
 */
export const DirectOAuthConfig = zod.object({
  issuer: zod.string().url(),
  clientId: zod.string(),
});
export type DirectOAuthConfig = zod.infer<typeof DirectOAuthConfig>;

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
export const RaidoOAuthState = zod.object({
  redirectUri: zod.string(),
  clientId: zod.string(),
});
export type RaidoOAuthState = zod.infer<typeof RaidoOAuthState>;

