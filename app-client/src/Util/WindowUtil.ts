

/** the location of the window without paths, params, etc.
 i.e. given window location "http://localhost:9090/blah?wibble=wobble#flobble"
 function returns "http://localhost:9090"
 */
export function serverLocationUrl(){
  return window.location.protocol + "//" + window.location.host
}

export function clearLocation(){
  navBrowserByAssign(serverLocationUrl());
}

export function reloadLocation(location: string){
  window.location.href = location;
  // this was originally written because replacing the location with a hash
  // param had no effect (I think the browser just tried to scoll instead
  // of refreshing the window.
  window.location.reload();
}

export function replaceLocation(location: string){
  window.location.replace(location);
}

/** I don't know if replace vs reload is an important distinction.
 */
export function navBrowserByReload(url: string){
  reloadLocation(url);
}

export function navBrowserByAssign(url: string){
  window.location.assign(url);
}

export function navBrowserBack(){
  window.history.back();
}

