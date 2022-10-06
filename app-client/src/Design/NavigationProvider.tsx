import React, { SyntheticEvent, useContext, useState } from "react";
import Fade from "@mui/material/Fade";
import { useLocationPathname } from "Util/Hook/LocationPathname";
import { normalisePath } from "Util/Location";

// value comes from eyeballing the drawer css in my browser, maybe there's
// a better place in the MUI API to get it from (theme defaults or something?)
const muiAppDrawerTimeout = 225;

/** The important part about this is that it controls exactly when the
 * pushState happens, this gives the AppDrawer (or any other temp stuff like
 * dialogs) time to transition away before ios "snapshots" the screen
 * (iOS then uses that snapshot when navigating "back" to the screen).
 * <p/>
 * Defined as 2 x appDrawer for no good reason other than I like the look of it.
 * This is very likely too slow and a result of "creator bias" - I think
 * it looks right because I wrote it and want to watch it happen.
 * <p/>
 * Should probably be appDrawerTimeout or less. Good design, especially as it
 * relates to animations, should be subliminal / barely noticable. My
 * personal desire that everyone should notice the cool screen transitions is
 * the antithesis of good design.
 */
export const navTime = muiAppDrawerTimeout * 2;

export interface NavigationState {
  pathname: string,
  navigatingTo: string | undefined,
  navigateTo: (to: string, event?: SyntheticEvent) => void,
  replace: (to: string, event?: SyntheticEvent) => void,
}

const NavigationContext = React.createContext({} as NavigationState );
export const useNavigation = ()=> useContext(NavigationContext);

export function NavigationProvider(props: {children: React.ReactNode}){
  const location = useLocationPathname();
  const [navigatingTo, setNavigatingTo] = useState(
    undefined as string | undefined );

  const navigateTo = React.useCallback((to: string, event?: SyntheticEvent)=> {
    event?.preventDefault();
    setNavigatingTo(to);
    setTimeout(()=>{
      location.pushState(to);
      setNavigatingTo(undefined);
    }, navTime);
  }, [location]);

  const replace = React.useCallback((to: string, event?: SyntheticEvent)=> {
    event?.preventDefault();
    setNavigatingTo(to);
    setTimeout(()=>{
      location.replaceState(to);
      setNavigatingTo(undefined);
    }, navTime);
  }, [location]);

  return <NavigationContext.Provider value={{
    pathname: location.pathname,
    navigatingTo,
    navigateTo:navigateTo,
    replace
  }}>
    {props.children}
  </NavigationContext.Provider>;
}

/**
 * Provides a nice "cross-fade" transition when navigating to a new page.
 */
export function NavTransition(props: {
  isPagePath: (pathname: string)=>NavPathResult,
  title: string,
  children: React.ReactNode
}){
  const nav = useNavigation();
  const {title, isPagePath} = props;
  const currentPage = isPagePath(nav.pathname);
  /* it's important to know which "direction" the navigation is going; so that
  we can transition "out of" or "away from" the "old" screen, while
  simultaneously transitioning "in" or "to" the "new" screen. */
  let isNavToThis = false;
  let isNavAway = false;
  if( nav.navigatingTo ){
    isNavToThis = isPagePath(nav.navigatingTo).isPath;
    if( !isNavToThis ){
      isNavAway = true;
    }
  }

  if( currentPage.isPath ){
    window.document.title = title;
  }

  /* "absolute" is what forces the two components to display over the top of
   each other. But we only need that while both the old and new screens are
   being shown; after that only the new screen is visible, so we don't need
   absolute positioning.
   This logic was added while I was frobbing styles during a failed attempt
   to fix the "scrollbar jumping" issue.
   Though I didn't fix the content jumping issue - the logic of only using
   absolute position during the transition makes sense to me. */
  const fadePosition = !!nav.navigatingTo ? "absolute" : "static";

  return <Fade
    timeout={navTime}
    in={!isNavAway && (isNavToThis || currentPage.isPath)}
    /* Each NavTransition is intended to occupy the same position on screen;
    without this, the "from" and "to" screen will display next to each other
    instead of in the same place.  It may be necessary to set "relative"
    somewhere in your element hierarchy if using this code outside of Cabbage.*/
    style={{position: fadePosition, width: "100%"}}
  >
    <div>
      {/*div is necessary if using Slide transition */}
      { (isNavToThis || currentPage.isPath) &&
        props.children
      }
    </div>
  </Fade>;
}

export function parsePageSuffixParams<TPageParams>(
  nav: NavigationState,
  isPath: (path: string)=>NavPathResult,
  parse: (suffix:string)=>TPageParams,
): TPageParams {
  if( nav.navigatingTo ){
    const parseResult = isPath(nav.navigatingTo);
    if( parseResult.isPath ){
      return parse(parseResult.pathSuffix);
    }
  }

  const parseResult = isPath(nav.pathname);
  if( parseResult.isPath ){
    return parse(parseResult.pathSuffix)
  }
  else {
    throw new Error("couldn't parse the page params from nav path");
  }
}

export function parseOptionalPageSuffixParams<TPageParams>(
  nav: NavigationState,
  isPath: (path: string)=>NavPathResult,
  parse: (suffix:string)=>TPageParams,
): TPageParams | undefined {
  if( nav.navigatingTo ){
    const parseResult = isPath(nav.navigatingTo);
    if( parseResult.isPath ){
      return parse(parseResult.pathSuffix);
    }
  }

  const parseResult = isPath(nav.pathname);
  if( parseResult.isPath ){
    return parse(parseResult.pathSuffix)
  }
  
  return undefined;
}

export type NavPathResult = {
  isPath: false,
} | {
  isPath: true,
  pathSuffix: string,
}

export function isPagePath(
  pathname: string, 
  pageUrl: string,
  debug: boolean = false,
): NavPathResult{
  // this removes any trailing slash
  let normalised = normalisePath(pathname);
  if (debug) console.log("isPagePath", normalised, pageUrl);
  if( !normalised.startsWith(pageUrl) ){
    if (debug) console.log("!startsWith");
    return {isPath: false};
  }

  if( normalised.length === pageUrl.length ){
    if (debug) console.log("exact match");
    // path is exact match, no suffix
    return {
      isPath: true,
      pathSuffix: "",
    }
  }
  else if( normalised[pageUrl.length] !== '/' ){
    if (debug) console.log("substring clash");
    /* if startsWith() matches, but the next char is not '/', it's probably 
    a sub-string clash, like: "/users", "/user/1234". */
    return {isPath: false,}
  }
  let suffix = normalised.substring(pageUrl.length+1);
  return {
    isPath: true,
    pathSuffix: suffix,
  }
}
