import React from "react";
import {RaidoOAuthState} from "Shared/ApiTypes";
import {useSignInContext} from "Auth/SignInContext";
import {oauthCodeGrantFlow} from "Auth/Constant";
import {Config} from "Config";
import {Button, Card, CardContent, CardHeader, Grid, IconButton, Typography} from "@mui/material";
import {Google, Help as HelpIcon} from "@mui/icons-material";
import {AustraliaSvgIcon, OrcidSvgIcon} from "Component/Icon";
import Tooltip from "@mui/material/Tooltip";

const googleAction = "google-direct";
const aafAction = "aaf-direct";
const orcidAction = "orcid-direct";

export function SignInContainer() {

    const signInContext = useSignInContext();

    async function googleSignIn() {
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
            window.location.assign(loginUrl);
        } catch (err) {
            signInContext.setAction(undefined);
            throw err;
        }
    }


    async function aafSignIn() {
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
            window.location.assign(loginUrl);
        } catch (err) {
            signInContext.setAction(undefined);
            throw err;
        }
    }

    async function orcidSignIn() {
        const state: RaidoOAuthState = {
            redirectUri: window.location.href,
            clientId: Config.orcid.clientId,
        }
        signInContext.setAction(orcidAction);

        let redirectUri = `${Config.raidoIssuer}/idpresponse`
        if (Config.environmentName === "dev") {
            console.warn("Orcid requires https, see `local-orcid-signin.md`");
        }

        try {
            let loginUrl = `${Config.orcid.authorizeUrl}` +
                `?client_id=${Config.orcid.clientId}` +
                `&scope=${encodeURIComponent(Config.orcid.authnScope)}` +
                // we're mixing between authorize and OIDC implicit flows, could be bad
                `&response_type=${oauthCodeGrantFlow}` +
                `&redirect_uri=${redirectUri}` +
                `&state=${formatStateValue(state)}`;
            window.location.assign(loginUrl);
        } catch (err) {
            signInContext.setAction(undefined);
            throw err;
        }
    }

    return (
        <Card>
            <CardHeader
                action={
                    <Tooltip title={
                        <>
                            <Typography variant="body2" gutterBottom>
                                You can sign in either directly with your personal Google
                                or ORCID account,
                                or via the AAF if your organisation has an agreement.
                            </Typography>
                            <Typography variant="body2">
                                Once you've signed in and authenticated yourself, you will be able to
                                submit a request for a specific institution to authorize your usage of
                                the RAiD app with their data.
                            </Typography>
                        </>
                    }>
                        <IconButton aria-label="help">
                            <HelpIcon/>
                        </IconButton>

                    </Tooltip>
                }
                title="RAiD Sign-in"
                subheader="Please select your preferred sign-in method"
            />
            <CardContent>
                <Grid container direction="row" spacing={2} justifyContent="center" alignItems="center">
                    <Grid item>
                        <Button variant="contained" startIcon={<Google/>} onClick={googleSignIn}>
                            Google
                        </Button>
                    </Grid>
                    <Grid item>
                        <Button variant="contained" startIcon={<AustraliaSvgIcon/>} onClick={aafSignIn}>
                            AAF
                        </Button>
                    </Grid>
                    <Grid item>
                        <Button variant="contained" startIcon={<OrcidSvgIcon/>} onClick={orcidSignIn}>
                            ORCID
                        </Button>
                    </Grid>
                </Grid>
            </CardContent>
        </Card>
    )
}

function encodeBase64(plainText: string, encodeUri = false): string{
    let base64Encoded = btoa(plainText);
    if( !encodeUri ){
        return base64Encoded;
    }

    return encodeURIComponent(base64Encoded);
}

function formatStateValue(state: RaidoOAuthState): string {
    let base64 = encodeBase64(JSON.stringify(state));
    /* the TwitterHandler was dying when the base64 encoding padded with `==`.
    The request never reached the lambda, AWS was returning a 400 error
    without invoking it.  At a guess, the AWS functionUrl/lambda infra is trying
    to parse out the query string parameters to pass in the Lambda context and
    failing because those `=` chars were causing it to choke.
    We don't need to "un-uriEncode" on the server because we use those lambda
    context params and they've already been decoded for us by AWS.
    I never saw a problem from the other IdProviders, but I decided to use this
    method to encode their state anyway - it makes sense. */
    return encodeURIComponent(base64);
}