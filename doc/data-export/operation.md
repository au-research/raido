To run, invoke the main method from
[AgencyPublicDataExport](/api-svc/spring/src/main/java/raido/export/AgencyPublicDataExport.java).

To configure general spring properties, set env variables or config
properties in `~/.config/raido/api-svc-env.properties`,
defined by the various beans in the
[environment](/api-svc/spring/src/main/java/raido/apisvc/spring/config/environment/)
package.

Unfortunately, because we're booting most of our spring beans except the web
stuff - it means some of the configuration checks will required you to set
values for "mandatory" properties, even though the export may not currently use
that functionality.

When running in a local dev environment, the export will just pick up your usual
config from your `~/.config/raido` files - assuming you run that way.

When running as a codebuild, we provide fake values for config that is not
currently used, see codebuild project in DEMO for an example.