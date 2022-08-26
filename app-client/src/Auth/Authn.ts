import jwtDecode from "jwt-decode";

export function getSocialRedirectIdToken(): string|undefined{
  const parsedHash = new URLSearchParams(
    window.location.hash.substring(1) // skip the first char (#)
  );

  let idToken = parsedHash.get("id_token");
  if( !idToken ){
    return undefined;
  }

  // don't leave stuff (tokens, params, etc.) in the url after a SSO redirect
  window.location.hash = "";
  
  return idToken;
}

export async function findSignInIdToken():Promise<string | undefined>{
  let idToken:string|undefined = getSocialRedirectIdToken();
  if( idToken ){
    console.log("found social idToken", idToken.slice(-10), jwtDecode(idToken));
    return idToken;
  }

  console.log("found no idToken");
  return undefined;
}
