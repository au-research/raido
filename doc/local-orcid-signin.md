Orcid won't allow you to set `http://localhost:8080` as a redirect uri, they
demand `https`.

My workaround for local testing:
* configure orcid to allow `https://localhost:6080/idpresponse` as an 
  accepted redirect uri
* run a local proxy:
  * `npx local-ssl-proxy --source 6080 --target 8080`
  * this acts a proxy for the api-svc, using a self-signed certificate 
  * you will have to click "advanced" and accept to actually login this way
* in [Config.ts](../app-client/src/Config.ts) set 
  `raidoIssuer: "https://localhost:6080",` in the `devConfig`
* in `~/.config/raido/api-svc-env.properties`, set
  * `RaidoAuthn.serverRedirectUri=https://localhost:6080/idpresponse`
  * this forces the api-svc use the https proxy for the redirect uri 
  (this will break other id providers in your local environment, unless you 
  configure `https://localhost:6080/idpresponse` as valid redirect uris for 
  google and AAF too.
    * I have added this as a valid redirectURL for the google demo OAuth config

 