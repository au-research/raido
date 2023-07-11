The export is run as a docker container via  CodeBuild project, see:
* [/api-svc/docker/data-export](/api-svc/docker/data-export)
* [AgencyPublicDataExport](https://github.com/au-research/raido-v2-aws-private/blob/main/raido-root/lib/demo/raido/RaidoDbCodeBuild.ts)

---

To run locally via IDE or similar, invoke the main method from
[AgencyPublicDataExport](/api-svc/spring/src/main/java/raido/export/AgencyPublicDataExport.java).

To configure general spring properties, set env variables or config
properties in `~/.config/raido/api-svc-env.properties`,
defined by the various beans in the
[environment](/api-svc/spring/src/main/java/raido/apisvc/spring/config/environment/)
package.

When running in a local dev environment, the export will just pick up your usual
config from your `~/.config/raido` files - assuming you run that way.
