import React from "react";
import { navBrowserByAssign, serverLocationUrl } from "Util/WindowUtil";
import { ContainerCard } from "Design/ContainerCard";
import { PrimaryButton } from "Component/AppButton";
import { RaidoOAuthState } from "Shared/ApiTypes";
import { encodeBase64 } from "Util/Encoding";
import { useSignInContext } from "Auth/SignInContext";
import { HelpPopover } from "Component/HelpPopover";
import { aaf, google, oauthCodeGrantFlow } from "Auth/Constant";
import { Config } from "Config";
import { Typography } from "@mui/material";
import { Google } from "@mui/icons-material";

const googleAction = "google-direct";
const aafAction = "aaf-direct";
 
export function SignInContainer(){
  //const serverInfo = useServerInfo();

  const signInContext = useSignInContext();

  async function googleSignIn(){
    const state: RaidoOAuthState = {
      // this redirectUril is about the lambda redirect back our client
      redirectUri: serverLocationUrl()
    }
    signInContext.setAction(googleAction);
    try {
      let loginUrl = `${google.authorize}` +
        `?client_id=${Config.googleClientId}` +
        `&scope=${encodeURIComponent(google.authnScope)}` +
        `&response_type=${oauthCodeGrantFlow}` +
        /* if the user accepts, google will redirect the browser to this uri,
        which will use the google code to generate an id_token and redirect 
        the browser back to whatever is in the state.redirectUri */
        `&redirect_uri=${Config.raidoIssuer}/idpresponse` +
        `&state=${formatStateValue(state)}`;
      navBrowserByAssign(loginUrl);
    }
    catch( err ){
      signInContext.setAction(undefined);
      throw err;
    }
  }


  async function aafSignIn(){
    // this is not an OIDC sign-in, github uses `,` to separate scopes
    const state: RaidoOAuthState = {
      redirectUri: serverLocationUrl()
    }
    signInContext.setAction(aafAction);
    
    try {
      let loginUrl = `${aaf.authorize}` +
        `?client_id=${Config.aafClientId}` +
        `&scope=${encodeURIComponent(aaf.authnScope)}` +
        `&response_type=${oauthCodeGrantFlow}` +
        `&redirect_uri=${Config.raidoIssuer}/idpresponse` +
        `&state=${formatStateValue(state)}`;
      navBrowserByAssign(loginUrl);
    }
    catch( err ){
      signInContext.setAction(undefined);
      throw err;
    }
  }


  const disabled = !!signInContext.action;
  return <ContainerCard title={"Sign-in"} action={<DirectHelp/>}> 
    <div style={{display: "grid", 
      gridTemplateColumns: "8em 8em",
      justifyContent: "center",
      columnGap: "1em", rowGap: "1em",
      // the textspan following was too cramped
      marginBottom: ".5em"
    }}>
      <PrimaryButton startIcon={<Google/>}
        isLoading={signInContext.action === googleAction}
        disabled={disabled} onClick={googleSignIn}
      >
        Google
      </PrimaryButton>
      <PrimaryButton  
        isLoading={signInContext.action === aafAction} 
        disabled={disabled} onClick={aafSignIn}
      >
        AAF
      </PrimaryButton>
    </div>
  </ContainerCard>
}

function formatStateValue(state: RaidoOAuthState):string{
  let base64 = encodeBase64(JSON.stringify(state));
  /* the TwitterHandler was dying when the base64 encoding padded with `==`.
  The request never reached the lambda, AWS was returning a 400 error 
  without invoking it.  At a guess, the AWS funtionUrl/lambda infra is trying 
  to parse out the query string parameters to pass in the Lambda context and 
  failing because those `=` chars were causing it to choke.  
  We don't need to "un-uriencode" on the server because we use those lambda 
  context params and they've already been decoded for us by AWS.
  I never saw a problem from the other IdProviders, but I decided to use this 
  method to encode their state anyway - it makes sense. */
  return encodeURIComponent(base64);
}

function DirectHelp(){
  return <HelpPopover content={
    <Typography>
      <Typography>
        You can sign in either directly with Google or via the AAF if your
        organisation has an agreement.
      </Typography>
      <Typography>
        Once you've signed in, you can request that an institution authorize
        your usage of Raido.
      </Typography>
    </Typography>
  }/>;
}