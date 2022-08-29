import {
  AuthorizeUserResponse,
  AuthzTokenPayload,
} from "Shared/ApiTypes";
import jwtDecode from "jwt-decode";
import { parseJwtDate, parseServerDate } from "Util/DateUtil";
import { ErrorInfo, forceError } from "Error/ErrorUtil";
import { AuthorizedSession } from "Auth/AuthProvider";
import { authApi } from "Api/AuthApi";

const accessTokenStorageKey = "raidoAccessToken";

function debugAuthzResponse(auth: AuthorizeUserResponse){
  if( !auth.succeeded ){
    return "failed";
  }
  
  return "succeeded - " + auth.accessToken.slice(-10); 
}

export function saveAccessTokenToStorage(accessToken: string){
  localStorage.setItem(accessTokenStorageKey, accessToken);
}

export function clearAccessTokenFromStorage(){
  localStorage.removeItem(accessTokenStorageKey);
}

export function getAuthSessionFromStorage(): undefined | AuthorizedSession{
  const storedAccessToken = localStorage.getItem(accessTokenStorageKey);

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


