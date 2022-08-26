import React, { useContext } from "react";
import { LargeContentMain, SmallContentMain } from "Design/LayoutMain";
import { SmallPageSpinner } from "Component/SmallPageSpinner";
import { ErrorInfoComponent } from "Error/ErrorInforComponent";
import { ErrorInfo, isErrorInfo } from "Error/ErrorUtil";
import { AuthzTokenPayload } from "Shared/ApiTypes";
import {
  authorizeWithServer,
  getAuthSessionFromStorage,
  signOutUser
} from "Auth/Authz";
import { findSignInIdToken } from "Auth/Authn";
import { IntroContainer } from "Auth/IntroContainer";
import { SignInContainer } from "Auth/SignInContainer";
import { useLocationPathname } from "Util/Hook/LocationPathname";
import { SignInContext } from "Auth/SignInContext";

export interface AuthState {
  signOut: () => void,
  session: AuthorizedSession,
}

export interface AuthorizedSession {
  payload: AuthzTokenPayload,
  accessTokenExpiry: Date,
  accessToken: string,
}

const AuthContext = React.createContext(
  undefined as unknown as AuthState);

/** If `use()` is called when not underneath the context provider,
 * they will get an error. */
export const useAuth = () => {
  let ctx = useContext(AuthContext);
  if( !ctx ){
    throw new Error("No AuthenticationProvider present in component hierarchy");
  }
  return ctx;
};

type ProviderState =
  // in-progress states
  {current: "init"} |
  {current: "authenticating"} |
  {current: "authorizing"} |
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
}){
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

    /* if they don't have a previous valid sign-in session and there's no 
    idToken from signing-in, then the user needs to sign-in. */
    const idToken = await findSignInIdToken();
    if( !idToken ){
      setState({current: "not-signed-in"});
      return;
    }

    /* verify user deatils and exchange the Cognito idToken for our 
    custom accessToken */
    setState({current: "authorizing"});
    const authzResult = await authorizeWithServer(idToken);
    if( isErrorInfo(authzResult) ){
      setState({
        current: "error", error: authzResult
      });
      return;
    }

    setState({current: "signed-in", authSession: authzResult});

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

  window.document.title = "Zinc - sign in";

  if( state.current === "init" || state.current === "authenticating" ){
    return <SmallPageSpinner message={"Signing in"}/>
  }

  if( state.current === "authorizing" ){
    return <SmallPageSpinner message={"Authorizing"}/>
  }

  if( state.current === "signing-out" ){
    return <SmallPageSpinner message={"Signing out"}/>
  }

  if( state.current === "error" ){
    return <ErrorInfoComponent error={state.error}/>
  }

  if( state.current === "not-signed-in" ){
    return <NotSignedInContent
      /* once the user has actually succeeded signing in, this logic 
       will be able to pick that up from the userpool or url. */
      onSignInSucceeded={checkLoginState}
    />
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
    <IntroContainer/>
    <SmallContentMain>
      <SignInContext.Provider value={{
        action: signInAction,
        setAction: setSignInAction
      }}>
        <SignInContainer/>
      </SignInContext.Provider>
    </SmallContentMain>
  </LargeContentMain>
}

