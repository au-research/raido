
/** Helps manage configuration values across multiple environments.
 * <p>
 * The config values used by the app are decided when this file is run (i.e.
 * when the app is loaded into the browser), based on the value of
 * environmentName, which is compiled in at build time via the
 * REACT_APP_RAIDO_ENV environment variable.
 * <p>
 * Config values are embedded into the raw client javascript artifact - it is
 * designed only for static, publicly visible config (i.e. not per user stuff
 * and definitely not secrets).
 * <p>
 * Config is not suitable for secrets like API keys or other sensitive data.
 * <p>
 * EnvironmentConfig is things we expect to change between environments.
 * <p>
 * Note, often there'll be lots of config that ends up using the same values
 * across most environments. Avoid the temptation to re-invent the concept of  
 * "shared" environment configuration.  
 * Each config (`prod` / `demo` etc.) should be reviewable as a stand-alone 
 * unit (in a non-IDE context).
 * Don't force reviewers to guess what the `prod` or `test` config looks like 
 * by mentally reproducing your "shared config merging logic".  Preventing 
 * accidental/unexpected usage of bad config (e.g. a test or prod environment 
 * accidentally using a wrong DB or other system dependency) is more important
 * than your personal obsession with the DRY principle.
 */

const log = console;

/** Defines what the known environments are. */
type EnvironmentName = "prod" | "demo" | "ci" | "dev";

export interface AuthnConfig {
  clientId: string,
  authorizeUrl: string,
  authnScope: string,
}

/**
 couldn't figure out how to use QueryObserverOptions for this, so just settled
 on a simplified version cut down for what we actually want to set.
 */
export interface ReactQueryConfig {
  retry: boolean | number,
  refetchOnWindowFocus: boolean | 'always',
}


export interface EnvironmentConfig {
  /** identifies the environment */
  environmentName: EnvironmentName,

  /** is this a "production" environment (usually there's only one)? 
   * If you feel the need to have switchable business logic based off of
   * your environment, this is what it should be predicated on 
   * (i.e not "if config.envName === "prd").
   */
  isProd: boolean,

  signInWarning?: string, 
  aaf: AuthnConfig,
  google: AuthnConfig,
  orcid: AuthnConfig,
  
  raidoIssuer: string,
  /** The hostname to use to call the api-svc endpoints. 
  Generally empty string, so that API calls are a relative to current host.
  But you can use a different host if desired (but remember to configure CORS
  headers if you want to do that). */
  raidoApiSvc: string,

  authApiQuery?: ReactQueryConfig,
  publicApiQuery?: ReactQueryConfig,
}

function initConfig(){
  const newConfig = {
    ...buildConfig,
    ...chooseEnvironmentConfig(process.env.REACT_APP_RAIDO_ENV),
  };

  log.debug("Application config", process.env.REACT_APP_RAIDO_ENV, newConfig);
  return newConfig;
}

export const unknownCommitId = "unknown commit";
export const unknownBuildDate = "unknown";

const buildConfig = {
  /* expected to be populated from something like 
  `date --iso-8601=seconds --utc`, e.g. `2023-02-01T06:31:13+00:00` */
  buildDate: process.env.REACT_APP_BUILD_DATE_MS ?? "unknownBuildDate" ,
  version: process.env.REACT_APP_RAIDO_VERSION ?? "unknown version",
  gitCommit: process.env.REACT_APP_COMMIT_REF ?? unknownCommitId,
};


function chooseEnvironmentConfig(env: string | undefined){
  /* trim() because env var can get polluted with whitespace when set via
  npm scripts. */
  env = env?.toLowerCase()?.trim();
  if( env === 'prod' ){
    return prodConfig
  }
  else if( env === 'demo' ){
    return demoConfig;
  }
  else if( env === 'ci' ){
    return ciConfig;
  }
  else if( env === 'dev' ){
    return devConfig;
  }
  else {
    console.log("unknown env, using dev: ", env);
    return devConfig;
  }
}

const ciConfig: EnvironmentConfig = {
  environmentName: "ci",
  isProd: false,
  raidoIssuer: "https://demo.raido-infra.com",
  raidoApiSvc: "",
  aaf: {
    clientId: "",
    authorizeUrl: "",
    authnScope: "",
  },
  google: {
    clientId: "",
    authorizeUrl: "",
    authnScope: "",
  },
  orcid: {
    clientId: "",
    authorizeUrl: "",
    authnScope: "",
  },
};

const devConfig: EnvironmentConfig = {
  environmentName: "dev",
  isProd: false,
  // have to run the https proxy for this, see local-orcid-signin.md  
  //raidoIssuer: "https://localhost:6080",
  raidoIssuer: "http://localhost:8080",
  raidoApiSvc: "",
  aaf: {
    // https://aaf.freshdesk.com/support/solutions/articles/19000096640-openid-connect-
    clientId: "accaabfd-a7c8-4d36-9363-ea7342e24db5",
    authorizeUrl: "https://central.test.aaf.edu.au/providers/op/authorize",
    authnScope: "openid email profile",
  },
  google: {
    clientId: "112489799301-m39l17uigum61l64uakb32vjhujuuk73.apps.googleusercontent.com",
    authorizeUrl: "https://accounts.google.com/o/oauth2/v2/auth",
    authnScope: "openid email profile",
  },
  // this is not going to work, orcid require https and it's currently set
  // to demo.raido-infra.com anyway 
  orcid: {
    clientId: "APP-IZBIZ6O7XH9RFG0X",
    authorizeUrl: "https://orcid.org/oauth/authorize",
    authnScope: "openid",
  },
  // disable because kind of annoying in a development context
  authApiQuery: {
    retry: false,
    refetchOnWindowFocus: false,
  },
  publicApiQuery: {
    retry: false,
    refetchOnWindowFocus: false,
  },
};

const demoConfig: EnvironmentConfig = {
  environmentName: "demo",
  signInWarning: `The DEMO environment has been reset as of 2023-02-21.
    All previously created raids have been deleted.  
    All previously create API-keys have been deleted.
    You will need to submit a new authorisation request to use the system.
    Please send an email to contact@raid.org to let us know you need approval.`,
  isProd: false,
  raidoIssuer: "https://api.demo.raido-infra.com",
  raidoApiSvc: "",
  aaf: {
    clientId: "accaabfd-a7c8-4d36-9363-ea7342e24db5",
    authorizeUrl: "https://central.test.aaf.edu.au/providers/op/authorize",
    authnScope: "openid email profile",
  },
  google: {
    // OAuth client: raid.service@ardc.edu.au / ardc.edu.au / Raido production / Raido DEMO
    clientId: "333652356987-ecetlds8nprpf81er55jnva1750jputr.apps.googleusercontent.com",
    authorizeUrl: "https://accounts.google.com/o/oauth2/v2/auth",
    authnScope: "openid email profile",
  },
  orcid: {
    clientId: "APP-207226CSNVZ1ZSQM",
    authorizeUrl: "https://sandbox.orcid.org/oauth/authorize",
    authnScope: "openid",
  },
  authApiQuery: {
    retry: 1,
    refetchOnWindowFocus: true,
  },
  publicApiQuery: {
    retry: false,
    refetchOnWindowFocus: false,
  },
};

const prodConfig: EnvironmentConfig = {
  environmentName: "prod",
  isProd: true,
  raidoIssuer: "https://prod.raido-infra.com",
  raidoApiSvc: "",
  signInWarning: `Note that this service is not yet live and contains
   no raid data.`,
  aaf: {
    /* This is the PROD client ID allocated by AAF via ticket
    https://aaf.freshdesk.com/helpdesk/tickets/9910 */
    clientId: "205290b0-8bd6-4d53-8605-d0237636b235",
    authorizeUrl: "https://central.aaf.edu.au/providers/op/authorize",
    authnScope: "openid email profile",
  },
  google: {
    // OAuth client: raid.service@ardc.edu.au / ardc.edu.au / Raido production / Raido PROD
    clientId: "333652356987-6n1dlouree79qbffrtgqd9fit6phkvs5.apps.googleusercontent.com",
    authorizeUrl: "https://accounts.google.com/o/oauth2/v2/auth",
    authnScope: "openid email profile",
  },
  orcid: {
    // this is the DEMO client ID, created under STO's ORCiD account
    clientId: "APP-IZBIZ6O7XH9RFG0X",
    authorizeUrl: "https://orcid.org/oauth/authorize",
    authnScope: "openid",
  },
  authApiQuery: {
    retry: 1,
    refetchOnWindowFocus: true,
  },
  publicApiQuery: {
    retry: false,
    refetchOnWindowFocus: false,
  },
};


export const Config: EnvironmentConfig & typeof buildConfig = initConfig();

