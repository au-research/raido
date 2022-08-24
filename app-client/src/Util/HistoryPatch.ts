/**
 Facilitates patching window.history state functions in so that many listers 
 can be added and removed (`removeListener()`)over time.
 Not sure why I did `changeListeners` array - why not just participate
 in the delegation chain, as with `uninstallBrowserHistoryStateHandler()`?
 
 Note: `popstate` is fired by the browser when user does something that 
 causes the location to change (forward, back button, etc).
 It *does not* get fired when the standard pushState()/replaceState()
 functions are invoked.

 See https://stackoverflow.com/a/4585031
 https://developer.mozilla.org/en-US/docs/Web/API/Window/popstate_event
 */
const StandardEventId = {
  popstate: "popstate",
};

const changeListeners: Array<Function> = [];

interface OriginalFunctions {
  pushStateFn : WindowHistoryFn,
  replaceStateFn : WindowHistoryFn,
  // convenience functions so originals can be invoked easily
  pushState: WindowHistoryFn;
  replaceState: WindowHistoryFn;
}

export let originalFunctions: OriginalFunctions|undefined = undefined;

function notifyListeners(){
  changeListeners.forEach(i=> i())
}

export function addListener(  
  onHistoryStateChange: ()=>void
){
  if( !originalFunctions ){
    installBrowserHistoryStateHandler()
  }
  
  if( changeListeners.includes(onHistoryStateChange) ){
    console.warn("changeListeners already contains function", 
      onHistoryStateChange);
    return;
  }
  
  changeListeners.push(onHistoryStateChange);
}

export function removeListener(fn: Function){
  const index = changeListeners.indexOf(fn);
  if( index === -1 ){
    console.warn("removeListener() called but function not in array", fn);
    return;
  }
  changeListeners.splice(index, 1);
}

export function uninstallBrowserHistoryStateHandler(){
  if( !originalFunctions ){
    console.warn("uninstallBrowserHistoryStateHandler() called, but never installed");
    return;
  }
  
  window.removeEventListener(
    StandardEventId.popstate, notifyListeners);
  window.history.pushState = originalFunctions.pushStateFn;
  window.history.replaceState = originalFunctions.replaceStateFn;
  
  // clear the array - https://stackoverflow.com/a/1232046/924597
  changeListeners.length = 0;
}

/** Sets up the browser history object so that the handler will be called when 
 any code calls pushState() or replaceState() or the user takes an action that
 fires the "popstate" event.

 @return function that will uninstall or undefined if no patching happened 
 because it's already been called.
 */
export function installBrowserHistoryStateHandler(): void{
  if( originalFunctions ){
    console.warn("installBrowserHistoryStateHandler() called twice");
    return;
  }

  // fire when client code calls history.push()/replace()
  const originalPushState = patchHistoryPushState(notifyListeners);
  const originalReplaceState = patchHistoryReplaceState(notifyListeners);

  // fire when browser publishes a popstate event
  window.addEventListener(StandardEventId.popstate, notifyListeners);

  /* it's possible that an update has occurred between render and the effect
   handler being attached above, so we run a manual check here on mount to 
   catch those possible updates.
   Based on: https://gist.github.com/bvaughn/e25397f70e8c65b0ae0d7c90b731b189  
   */
  notifyListeners();

  // saved for uninstallation
  originalFunctions = {
    pushStateFn: originalPushState,
    replaceStateFn: originalReplaceState,
    pushState: originalPushState.bind(window.history),
    replaceState: originalReplaceState.bind(window.history),
  };
}

/* the below functions could be a single generic function that works for both 
push and replace, but I'm too lazy to bother.
*/
type WindowHistoryFn =
  (data: any, title: string, url?: (string | null)) => void;

function patchHistoryPushState(onPushState: WindowHistoryFn): WindowHistoryFn{
  const original: WindowHistoryFn = window.history.pushState;

  window.history.pushState = function(
    data: any, title: string, url?: (string | null)
  ): void{
    original.apply(this, [data, title, url]);
    onPushState(data, title, url);
  };

  return original;
}

function patchHistoryReplaceState(
  onReplaceState: WindowHistoryFn
): WindowHistoryFn{
  const original: WindowHistoryFn = window.history.replaceState;

  window.history.replaceState = function(
    data: any, title: string, url?: (string | null)
  ): void{
    original.apply(this, [data, title, url]);
    onReplaceState(data, title, url);
  };
  
  return original;
}
