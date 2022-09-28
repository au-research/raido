import React, {useCallback, useEffect, useRef, useState} from "react";
import {
  addListener,
  originalFunctions,
  removeListener
} from "Util/HistoryPatch";
import {formatPath, recordToSearchString, searchStringToRecord} from "Util/Location";

export type SearchParams = Record<string, string|undefined>;

export interface LocationSearchState<T extends SearchParams> {
  /** tracks window.location.search */
  search: T,
  replaceState: (params: T) => void,
  pushState: (params: T) => void,
}

const LocationSearchContext = React.createContext<
  LocationSearchState<SearchParams> | undefined
>(undefined);

export function useLocationSearch<T extends SearchParams>(): 
LocationSearchState<T>{
  const context = React.useContext(LocationSearchContext);
  if (!context) {
    throw new Error('useLocationSearchContextT must be used' +
      ' underneath a LocationSearchProviderT element');
  }
  
  return context as any as LocationSearchState<T>;
}

/**
 This thing is currently designed for use on "in-page" tracking of page state
 against the search params (i.e. tracking changes to filter logic, where the 
 browser stays on the "current page" and we don't do nav transitions.
 The NavigationProvider doesn't understand this component (or search params at 
 all), so you can't really use search params for declaring parmeters that work 
 with page transitions. 
 */
export function LocationSearchProvider({children}: {
  children: React.ReactNode
}){
  const [search, setSearch] = useState(window.location.search);
  const replaceState = useCallback((params: SearchParams) => {
    if( !originalFunctions ){
      console.warn("replaceState callback invoked before listener installed");
      return;
    }
    originalFunctions.replaceState(null, "", 
      formatPath(window.location.pathname, params));
    setSearch(recordToSearchString(params));
  }, [setSearch]);
  const pushState = useCallback((params: Record<string, string|undefined>) => {
    if( !originalFunctions ){
      console.warn("pushState callback invoked before listener installed");
      return;
    }
    originalFunctions.pushState(null, "", 
      formatPath(window.location.pathname, params));
    setSearch(recordToSearchString(params));
  }, [setSearch]);

  const newSearch = searchStringToRecord(search);
  let initValue = {
    search: newSearch,
    replaceState,
    pushState };
  const state = useRef(initValue);

  /* intercepts forward/back browser actions ("popstate" event), or other 
  non-project javascript calls to window.history.pushState()/replaceState() */
  const onHistoryStateChange = React.useCallback(() => {
    setSearch(window.location.search);
  }, []);
  useEffect(() => {
    addListener(onHistoryStateChange);
    return () => removeListener(onHistoryStateChange);
  }, [onHistoryStateChange]);

  /* If path hasn't changed, don't make a new state, so Provider has same 
  `value` object and users of `useLocationPath()` will not be re-rendered.
  If path has changed, updating the `value` object is what triggers anything
  in our App that cares about the path (pages, menus, etc.) to re-render. */
  if( search !== recordToSearchString(state.current.search) ){
    state.current = {
      search: searchStringToRecord(search), 
      replaceState, 
      pushState
    };
  }

  return <LocationSearchContext.Provider
    value={state.current as any as LocationSearchState<SearchParams>}
  >{children}</LocationSearchContext.Provider>;
}

