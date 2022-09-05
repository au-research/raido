import { AuthzTokenPayload, } from "Shared/ApiTypes";
import jwtDecode from "jwt-decode";
import { parseJwtDate } from "Util/DateUtil";
import { AuthorizedSession } from "Auth/AuthProvider";

const accessTokenStorageKey = "raidoAccessToken";

/** If user turns off cookies, they will also not be able to write to 
 localStorage.  Just ignore errors, the app will still work. */
export function saveAccessTokenToStorage(accessToken: string){
  try {
    localStorage.setItem(accessTokenStorageKey, accessToken);
  }
  catch( e ){
    console.warn("could not save to local storage," +
      " probably because cookies turned off?");
  }
}

export function clearAccessTokenFromStorage(){
  try {
    localStorage.removeItem(accessTokenStorageKey);
  }
  catch( e ){
    console.warn("could not clear local storage," +
      " probably because cookies turned off?");
  }
}

export function getAccessTokenFromStorage(): string | null {
  try {
    return localStorage.getItem(accessTokenStorageKey)
  }
  catch( e ){
    console.warn("could not get from local storage," +
      " probably because cookies turned off?");
    return null;
  }
}

export function getAuthSessionFromStorage(): undefined | AuthorizedSession{
  const storedAccessToken = getAccessTokenFromStorage();

  if( !storedAccessToken ){
    return undefined;
  }
  
  const parseResult = parseAccessToken(storedAccessToken);
  if( !parseResult.succeeded ){
    console.warn("problem parsing accessToken from storage, clearing token.",
      parseResult.message, parseResult.decoded);
    clearAccessTokenFromStorage();
    return undefined;
  }

  return {
    accessToken: storedAccessToken,
    accessTokenExpiry: parseResult.accessTokenExpiry,
    payload: parseResult.payload
  };
}

export function parseAccessToken(accessToken: string):{
  succeeded: true,
  accessTokenExpiry: Date,
  payload: AuthzTokenPayload,
} | {
  succeeded: false,
  message: string,
  decoded: string|undefined,
}{
  const decoded: any = jwtDecode(accessToken);

  if( !decoded ){
    return {succeeded: false, message: "accessToken decode issue", decoded};
  }

  if( typeof decoded !== "object" ){
    return {succeeded: false, message: "decoded token is not object", decoded};
  }

  if( !decoded.clientId || typeof(decoded.clientId) !== "string" ){
    return {succeeded: false, 
      message: "no accessToken payload clientId", decoded};
  }

  if( !decoded.sub || typeof(decoded.sub) !== "string" ){
    return {succeeded: false, 
      message: "no accessToken payload sub", decoded};
  }

  if( !decoded.email  || typeof(decoded.email) !== "string" ){
    return {succeeded: false, message: "no accessToken payload email", decoded};
  }

  if( !decoded.role  || typeof(decoded.role) !== "string" ){
    return {succeeded: false, message: "no accessToken payload role", decoded};
  }

  //if( !decoded.userCreated  || typeof(decoded.userCreated) !== "string" ){
  //  return {succeeded: false, 
  //    message: "no accessToken payload userCreated", decoded};
  //}

  if( !decoded.exp || typeof(decoded.exp) !== "number" ){
    return {succeeded: false, 
      message: "malformed accessToken payload exp", decoded};
  }

  const accessTokenExpiry: Date|undefined = parseJwtDate(decoded.exp);
  if( !accessTokenExpiry ){
    return {succeeded: false, 
      message: "malformed accessToken payload exp", decoded};
  }

  if( accessTokenExpiry <= new Date() ){
    console.debug("accessTokenExpiry", accessTokenExpiry);
    return {succeeded: false, message: "accessToken is expired", decoded};
  }

  return {
    succeeded: true,
    accessTokenExpiry,
    payload: {
      ...decoded,
      ///* date needs to be converted since it was decoded from a JWT, not passed
      //through our API parsing routine. */
      //userCreated: parseServerDate(decoded.userCreated)
    },
  }
}

export async function signOutUser(): Promise<void>{
  clearAccessTokenFromStorage();
}


