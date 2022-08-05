Hasn't been added as a coding guideline or anything yet - but I've been 
adding all the properties that define our runtime environment in this 
package as `Props` classes (to differentiate them from `Config` classes which
are about Spring configuration).  Not sure this is a good idea or not.

Public fields are fine for simple values that are used directly by other
components / services.  Contrast with DataSourceProps where the fields are 
private with `createProps()` wrapper methods.

The actual names of the configuration values are all over the place (ie. the 
name of the properties that you use to define the values externally).  Probably
would be a good idea to standardise these.

Default values for environment configuration properties should be set for 
local development.  So minimal config is necessary to get a developer up and
running.

Real enviornments (TEST, DEMO / PROD) will have all their environment related 
properties set explicitly.  One reason for this is to make reviewing config
changes more self-contained - c.f. reading a PR diff and also having to reason
about or look up what the default values are. 