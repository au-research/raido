/** Routing infrastructure; fundamentally, it manages the `window.location`
 * for our app, all pages should use it when doing navigation actions.
 * <p>
 * The basic idea comes from the wouter library, it's just
 * converted to Typescript and factored a little differently (e.g. uses React
 * Context instead of an event listener chain).
 * Note that wouter has moved on and gotten more complicated since then.
 * https://github.com/molefrog/wouter/blob/a6c1eeda1ebc6b98d5ef4bffcfcfd43fae7937c3/use-location.js
 */
import React, {
  useCallback,
  useContext,
  useEffect,
  useRef,
  useState
} from "react";
import {
  addListener,
  originalFunctions,
  removeListener
} from "Util/HistoryPatch";
import {parsePathname} from "Util/Location";

export interface LocationPathnameState {
  /** tracks window.location.pathname */
  pathname: string,
  replaceState: (path: string) => void,
  pushState: (path: string) => void,
}

const LocationPathnameContext = React.createContext({} as LocationPathnameState);
export const useLocationPathname = () => useContext(LocationPathnameContext);

export function LocationPathnameProvider({children}: {
  children: React.ReactNode
}){
  const [pathname, setPathname] = useState(window.location.pathname);
  const replaceState = useCallback((path: string) => {
    if( !originalFunctions ){
      console.warn("replaceState callback invoked before listener installed");
      return;
    }
    console.log("LocationPathname.replaceState()");
    originalFunctions.replaceState(null, "", path);
    setPathname(parsePathname(path));
  }, [setPathname]);
  const pushState = useCallback((path: string) => {
    if( !originalFunctions ){
      console.warn("pushState callback invoked before listener installed");
      return;
    }
    originalFunctions.pushState(null, "", path);
    setPathname(parsePathname(path));
  }, [setPathname]);
  
  const state = useRef({pathname, replaceState, pushState});

  /* intercepts forward/back browser actions ("popstate" event), or other 
  non-project javascript calls to window.history.pushState()/replaceState() */
  const onHistoryStateChange = React.useCallback(() => {
    setPathname(window.location.pathname);
  }, []);
  useEffect(() => {
    addListener(onHistoryStateChange);
    return () => removeListener(onHistoryStateChange);
  }, [onHistoryStateChange]);

  /* If path hasn't changed, don't make a new state, so Provider has same 
  `value` object and users of `useLocationPath()` will not be re-rendered.
  If path has changed, updating the `value` object is what triggers anything
  in our App that cares about the path (pages, menus, etc.) to re-render. */
  if( pathname !== state.current.pathname ){
    state.current = {pathname, replaceState, pushState};
  }

  return <LocationPathnameContext.Provider value={state.current}>
    {children}
  </LocationPathnameContext.Provider>;
}
