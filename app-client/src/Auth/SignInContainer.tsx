import React from "react";
import { navBrowserByAssign, serverLocationUrl } from "Util/WindowUtil";
import { ContainerCard } from "Design/ContainerCard";
import { PrimaryButton } from "Component/AppButton";
import { RaidoOAuthState } from "Shared/ApiTypes";
import { encodeBase64 } from "Util/Encoding";
import { useSignInContext } from "Auth/SignInContext";
import { HelpPopover } from "Component/HelpPopover";
import { oauthCodeGrantFlow } from "Auth/Constant";
import { Config } from "Config";
import { Typography } from "@mui/material";
import { Google } from "@mui/icons-material";

const googleAction = "google-direct";
const aafAction = "aaf-direct";
const orcidAction = "orcid-direct";
 
export function SignInContainer(){
  //const serverInfo = useServerInfo();

  const signInContext = useSignInContext();

  async function googleSignIn(){
    const state: RaidoOAuthState = {
      // tells the IdP where to redirect to after user approval
      redirectUri: window.location.href,
      // this tells the server redirect url which IdP was used
      clientId: Config.google.clientId,
    }
    signInContext.setAction(googleAction);
    try {
      let loginUrl = `${Config.google.authorizeUrl}` +
        `?client_id=${Config.google.clientId}` +
        `&scope=${encodeURIComponent(Config.google.authnScope)}` +
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
    const state: RaidoOAuthState = {
      redirectUri: window.location.href,
      clientId: Config.aaf.clientId,
    }
    signInContext.setAction(aafAction);
    
    try {
      let loginUrl = `${Config.aaf.authorizeUrl}` +
        `?client_id=${Config.aaf.clientId}` +
        `&scope=${encodeURIComponent(Config.aaf.authnScope)}` +
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

  async function orcidSignIn(){
    const state: RaidoOAuthState = {
      redirectUri: window.location.href,
      clientId: Config.orcid.clientId,
    }
    signInContext.setAction(orcidAction);

    let redirectUri = `${Config.raidoIssuer}/idpresponse`
    if( Config.environmentName === "dev" ){
      alert("Orcid requires https, see `local-orcid-signin.md`");
      // you have to run the local https proxy for this to work
      redirectUri = "https://localhost:6080/idpresponse"
    }

    try {
      let loginUrl = `${Config.orcid.authorizeUrl}` +
        `?client_id=${Config.orcid.clientId}` +
        `&scope=${encodeURIComponent(Config.orcid.authnScope)}` +
        // we're mixing between authorize and OIDC implicit flows, could be bad
        `&response_type=${oauthCodeGrantFlow}` +
        `&redirect_uri=${redirectUri}` +
        `&state=${formatStateValue(state)}`;
      navBrowserByAssign(loginUrl);
    }
    catch( err ){
      signInContext.setAction(undefined);
      throw err;
    }
  }


  const disabled = !!signInContext.action;
  return <ContainerCard title={"Sign-in"} action={<OidcSignInHelp/>}> 
    <div style={{display: "grid", 
      // repeat tells grid to fill the width but wrap like a flex 
      gridTemplateColumns: "repeat(auto-fill, 8em)",
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
      <PrimaryButton  
        isLoading={signInContext.action === orcidAction} 
        disabled={disabled} onClick={orcidSignIn}
      >
        ORCiD
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

function OidcSignInHelp(){
  return <HelpPopover content={
    <>
      <Typography>
        You can sign in either directly with Google or Orcid, or via the AAF 
        if your organisation has an agreement.
      </Typography>
      <Typography>
        Once you've signed in and authenticated yourself, you will be able to 
        submit a request for a specific institution to authorize your usage of 
        the RAID app with their data.
      </Typography>
    </>
  }/>;
}