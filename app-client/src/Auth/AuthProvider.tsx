import React, { ReactNode, useCallback, useContext, useEffect } from "react";
import { LargeContentMain, SmallContentMain } from "Design/LayoutMain";
import { SmallPageSpinner } from "Component/SmallPageSpinner";
import { ErrorInfoComponent } from "Error/ErrorInforComponent";
import { ErrorInfo } from "Error/ErrorUtil";
import { AuthzTokenPayload, isAuthzTokenPayload } from "Shared/ApiTypes";
import {
  getAuthSessionFromStorage,
  parseAccessToken,
  saveAccessTokenToStorage,
  signOutUser
} from "Auth/Authz";
import { findSignInIdToken } from "Auth/Authn";
import { IntroContainer } from "Auth/IntroContainer";
import { SignInContainer } from "Auth/SignInContainer";
import { useLocationPathname } from "Util/Hook/LocationPathname";
import { SignInContext } from "Auth/SignInContext";
import { NotAuthorizedContent } from "Auth/NotAuthorizedContent";
import { EnvironmentBanner } from "Design/AppNavBar";

export interface AuthState {
  signOut: () => void,
  session: AuthorizedSession,
}

export function isAuthState(obj: any): obj is AuthState {
  return (
    obj !== null &&
    typeof obj === 'object' &&
    typeof obj.signOut === 'function' &&
    isAuthorizedSession(obj.session) 
  );
}

export interface AuthorizedSession {
  payload: AuthzTokenPayload,
  accessTokenExpiry: Date,
  accessTokenIssuedAt: Date,
  /* IMPROVE: this should not be saved in globally accessible memory in the 
  current browser context.  Either use a Service Worker or closure variable to
  protect it from malicious JS running on the page.
  https://blog.ropnop.com/storing-tokens-in-browser/#global-variable */
  accessToken: string,
}

export function isAuthorizedSession(obj: any): obj is AuthorizedSession {
  return (
    !!obj &&
    isAuthzTokenPayload(obj.payload) &&
    obj.accessTokenExpiry instanceof Date &&
    obj.accessTokenIssuedAt instanceof Date &&
    typeof obj.accessToken === "string"
  );
}

const AuthContext = React.createContext(
  undefined as unknown as AuthState);

/** If `use()` is called when not underneath the context provider,
 * they will get an error. */
export const useAuth = ():AuthState => {
  let ctx = useContext(AuthContext);
  if( !ctx ){
    throw new Error("No AuthenticationProvider present in component hierarchy");
  }
  return ctx;
};

// implemented for the ErrorDialog functionality, user might not be signed in 
export const useAuthInAnyContext = ():AuthState|undefined => {
  let ctx = useContext(AuthContext);
  if( !ctx ){
    return undefined;
  }
  return ctx;
};

type ProviderState =
  // in-progress states
  {current: "init"} |
  {current: "authenticating"} |
  {current: "not-authorized", accessToken: string} |
  {current: "signing-out"} |

  // terminal states
  {current: "not-signed-in"} |
  {current: "signed-in", authSession: AuthorizedSession} |
  {current: "error", error: ErrorInfo};

/**
 * Handles both Authentication and Authorization.
 */
export function AuthProvider({unauthenticatedPaths = [], children}: {
    unauthenticatedPaths?: ((pathname: string) => boolean)[]
    children: React.ReactNode,
  }
){
  //const serverInfo = useServerInfo();
  const {pathname} = useLocationPathname();
  const [state, setState] = React.useState<ProviderState>({current: "init"});
  const stateCache = React.useRef({} as AuthState);
  
  /**
   * - looks for a non-expired `accessToken` in local storage,
   *   otherwise looks for an `idToken` from signing in with email/social
   * - exchanges the idToken for an accessToken from the server
   * - save the accessToken to storage for next time
   */
  const checkLoginState = React.useCallback(async () => {
    /* look for a cached accessToken, from a previous session */
    setState({current: "authenticating"});
    const authSession = getAuthSessionFromStorage();
    if( authSession ){
      setState({current: "signed-in", authSession});
      return;
    }
    else {
      /* getAuthSessionFromStorage() returns undefined if there's nothing in
      storage and also if anything else goes wrong (malformed token, etc.) */
    }

    /* if they don't have a previous valid sign-in session and there's no 
    idToken from signing-in, then the user needs to sign-in. */
    const idToken = await findSignInIdToken();
    if( !idToken ){
      setState({current: "not-signed-in"});
      return;
    }

    /* For google and aaf - the api-svc redirects back to client with the 
    id_token set to the actual access_token.  Might have to do an intermediate
    step for Cognito. */

    /* verify user details and exchange the Cognito idToken for our 
    custom accessToken 
    setState({current: "authorizing"});
    const authzResult = await authorizeWithServer(idToken);
    if( isErrorInfo(authzResult) ){
      setState({
        current: "error", error: authzResult
      });
      return;
    }
   */
    
    const parseResult = parseAccessToken(idToken);
    
    if( parseResult.result === "failed" ){
      console.warn("problem parsing idToken returned from sign-in",
        parseResult.message, parseResult.decoded);
      setState({current: "error", error: {
        message: parseResult.message, 
        problem: parseResult.message, 
      }});
      return;
    }

    if( parseResult.result === "not-authorized" ){
      setState({
        current: "not-authorized", 
        accessToken: parseResult.accessToken
      });
      return;
    }
    
    /* IMPROVE: this is questionable from a security standpoint.
     Better to just save what clientId the user logged in through last time and 
     use that to redirect to the IDP to authenticate again.
     Proper OAuth2 Id Providers remember that the user has previously 
     approved the app and will just immediately (seamless to user) 
     redirect back to /idpresponse with a new auth code.
     Pros of storing token in storage:
     - faster login UX, no waiting for the redirect chain of:
      app-client -302-> IDP -302-> api-svc -302-> app-client
     - better DX, no IDP redirects during development - but we could make 
      this configurable, or even come up with an even better 
      "development only" authz token approach. */
    saveAccessTokenToStorage(idToken);
    
    setState({current: "signed-in", authSession: { 
      accessToken: idToken, 
      accessTokenExpiry: parseResult.accessTokenExpiry,
      accessTokenIssuedAt: parseResult.accessTokenIssuedAt,
      payload: parseResult.payload, 
    }});
    
  }, []);

  const onSignOutClicked = React.useCallback(async () => {
    setState({current: "signing-out"});
    await signOutUser();
    // this should result in "not-logged-in" state
    await checkLoginState();
  }, [checkLoginState]);

  React.useEffect(() => {
    //   noinspection JSIgnoredPromiseFromCall
    checkLoginState();
  }, [checkLoginState]);

  if( unauthenticatedPaths.some(it => it(pathname)) ){
    return null;
  }

  window.document.title = "Raido - sign in";

  if( state.current === "init" || state.current === "authenticating" ){
    return <SmallPageSpinner message={"Signing in"}/>
  }

  //if( state.current === "authorizing" ){
  //  return <SmallPageSpinner message={"Authorizing"}/>
  //}

  if( state.current === "signing-out" ){
    return <SmallPageSpinner message={"Signing out"}/>
  }

  if( state.current === "error" ){
    return <ErrorInfoComponent error={state.error}/>
  }

  if( state.current === "not-signed-in" ){
    return <NotSignedInContent
      /* once the user has actually succeeded signing in, this logic 
       will be able to pick that up from the userPool or url. */
      onSignInSucceeded={checkLoginState}
    />
  }

  if( state.current === "not-authorized" ){
    return <NotAuthorizedContent accessToken={state.accessToken}/>
  }
  // avoid unnecessary re-renders based on creating a new context value
  if( stateCache.current.signOut !== onSignOutClicked ||
    stateCache.current.session !== state.authSession 
  ){
    stateCache.current = {
      signOut: onSignOutClicked,
      session: state.authSession,
    }
  }
  
  /* User is successfully verified and successfully signed in, so now show the 
  rest of the app and provide the auth details via the useAuth() hook. */
  return <AuthContext.Provider value={stateCache.current}>
    {children}
  </AuthContext.Provider>;
}

function NotSignedInContent({onSignInSucceeded}: {
  onSignInSucceeded: () => void,
}){
  const [signInAction, setSignInAction] = React.useState(
    undefined as string | undefined);
  
  return <LargeContentMain>
    <EnvironmentBanner />
    <IntroContainer/>
    <SmallContentMain>
      <SignInContext.Provider value={{
        action: signInAction,
        setAction: setSignInAction
      }}>
        {/* Need to reset the action when page unloaded because if user hits
         back button on hte IDP page, the browser BF-cache will restore the 
         React state and buttons just sit there spinning/disabled. */}
        <PageHideListener onPageHide={()=> setSignInAction(undefined)}>
          <SignInContainer/>
        </PageHideListener>
      </SignInContext.Provider>
    </SmallContentMain>
  </LargeContentMain>
}

function PageHideListener({onPageHide, children}:{
  onPageHide: ()=>void,
  children: ReactNode
}){
  const onPageHideCb = useCallback((ev: PageTransitionEvent) => {
    onPageHide();
  }, [onPageHide]);
  
  useEffect(() => {
    window.addEventListener('pagehide', onPageHideCb);

    return () => {
      // cleanup
      window.removeEventListener('pagehide', onPageHideCb);
    }
  }, [onPageHideCb]);  
  
  return <>
    {children}
  </>
}