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

export interface EnvironmentConfig {
  /** identifies the environment */
  environmentName: EnvironmentName,

  /** is this a "production" environment (usually there's only one)? 
   * If you feel the need to have switchable business logic based off of
   * your environment, this is what it should be predicated on 
   * (i.e not "if config.envName === "prd").
   */
  isProd: boolean,

  aaf: AuthnConfig,
  google:AuthnConfig,
  raidoIssuer: string,
}

function initConfig(){
  const newConfig = {
    ...buildConfig,
    ...chooseEnvironmentConfig(process.env.REACT_APP_RAIDO_ENV),
  };

  log.debug("Application config", process.env.REACT_APP_RAIDO_ENV, newConfig);
  return newConfig;
}

const buildConfig = {
  buildDate: process.env.REACT_APP_BUILD_DATE_MS ?? "0",
  gitCommit: process.env.REACT_APP_COMMIT_REF ?? "unknown commit",
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
  aaf: {
    clientId: "",
    authorizeUrl: "",
    authnScope: "",
  },
  google: {
    clientId: "",
    authorizeUrl: "",
    authnScope: "",
  }
};

const devConfig: EnvironmentConfig = {
  environmentName: "dev",
  isProd: false,
  raidoIssuer: "http://localhost:8080",
  aaf: {
    // https://aaf.freshdesk.com/support/solutions/articles/19000096640-openid-connect-
    clientId: "accaabfd-a7c8-4d36-9363-ea7342e24db5",
    authorizeUrl: "https://central.test.aaf.edu.au/providers/op/authorize",
    authnScope: "openid email profile",
    //authnScope: "openid profile email" +
    //  " aueduperson eduperson_orcid eduperson_assurance eduperson_affiliation" +
    //  " eduperson_entitlement schac_home_organization",
  },
  google: {
    clientId: "112489799301-m39l17uigum61l64uakb32vjhujuuk73.apps.googleusercontent.com",
    authorizeUrl: "https://accounts.google.com/o/oauth2/v2/auth",
    authnScope: "openid email profile",
  }
};

const demoConfig: EnvironmentConfig = {
  environmentName: "demo",
  isProd: false,
  raidoIssuer: "https://demo.raido-infra.com",
  aaf: {
    clientId: "accaabfd-a7c8-4d36-9363-ea7342e24db5",
    authorizeUrl: "https://central.test.aaf.edu.au/providers/op/authorize",
    authnScope: "openid email profile",
  },
  google: {
    clientId: "112489799301-m39l17uigum61l64uakb32vjhujuuk73.apps.googleusercontent.com",
    authorizeUrl: "https://accounts.google.com/o/oauth2/v2/auth",
    authnScope: "openid email profile",
  }
};

const prodConfig: EnvironmentConfig = {
  environmentName: "prod",
  isProd: true,
  raidoIssuer: "",
  aaf: {
    clientId: "",
    authorizeUrl: "",
    authnScope: "",
  },
  google: {
    clientId: "",
    authorizeUrl: "",
    authnScope: "",
  }
};


export const Config: EnvironmentConfig & typeof buildConfig = initConfig();
