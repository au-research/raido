import React, {SyntheticEvent, useContext, useState} from "react";
import Fade from "@mui/material/Fade";
import {
  useLocationPathname
} from "Util/Hook/LocationPathname";

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
  isPath: (path: string)=>boolean,
  title: string,
  children: React.ReactNode
}){
  const nav = useNavigation();
  const {title, isPath} = props;
  const isOn = isPath(nav.pathname);
  /* it's important to know which "direction" the navigation is going; so that
  we can transition "out of" or "away from" the "old" screen, while
  simultaneously transitioning "in" or "to" the "new" screen. */
  let isNavToThis = nav.navigatingTo && isPath(nav.navigatingTo);
  let isNavAway = nav.navigatingTo && !isNavToThis;

  if( isOn ){
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
    in={!isNavAway && (isNavToThis || isOn)}
    /* Each NavTransition is intended to occupy the same position on screen;
    without this, the "from" and "to" screen will display next to each other
    instead of in the same place.  It may be necessary to set "relative"
    somewhere in your element hierarchy if using this code outside of Cabbage.*/
    style={{position: fadePosition, width: "100%"}}
  >
    <div>
      {/*div is necessary if using Slide transition */}
      { (isNavToThis || isOn) &&
        props.children
      }
    </div>
  </Fade>;
}