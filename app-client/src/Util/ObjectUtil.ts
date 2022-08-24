import * as React from "react";

const log = console;

export function safeStringify(o: any, indented: undefined|"2-indent"=undefined){
  if( o === undefined ){
    return "[undefined]";
  }

  if( o instanceof HTMLElement ){
    return "HTMLElement";
  }

  if( React.isValidElement(o) ){
    return "ReactElement";
  }

  /* All the conditions above aren't as exhaustive as they seem, once you
  realise that stringify will explode if any nested child property
  is any of those things either.
  Example: someone logs an ErrorInfo where the "message" property is a
  react node - boom: "TypeError: Converting circular structure to JSON".
  That's why the actual call must be inside a try/catch.
  */
  if( indented === "2-indent" ){
    try {
      return JSON.stringify(o, null, 2);
    } catch( e ){
      log.warn("could not stringify object", o, e);
      return "unknown";
    }
  }
  else {
    try {
      return JSON.stringify(o);
    } catch( e ){
      log.warn("could not stringify object", o, e);
      return "unknown";
    }
  }
}

/** return hashcode of given string.
 * Taken from https://github.com/darkskyapp/string-hash/blob/master/index.js
 * */
export function hash(str: string):number{
  let hash = 5381;
  let i = str.length;

  while( i ){
    hash = (hash * 33) ^ str.charCodeAt(--i);
  }

  /* JavaScript does bitwise operations (like XOR, above) on 32-bit signed
   * integers. Since we want the results to be always positive, convert the
   * signed int to an unsigned by doing an unsigned bitshift. */
  return hash >>> 0;
}

export function appendUrlSlash(val: string):string{
  if( val.endsWith("/") ){
    return val;
  }
  else {
    return val + "/";
  }
}

