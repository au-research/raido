This project is for building a container that can run the 
[AgencyPublicDataExport](../../spring/src/main/java/raido/cmdline/AgencyPublicDataExport.java)
data export.

It just builds a docker image (tagged with `latest`), we build the image each 
time we run it, see
[AgencyPublicDataExport](https://github.com/au-research/raido-v2-aws-private/blob/main/raido-root/lib/demo/raido/RaidoDbCodeBuild.ts)
codebuild project for an example.

---

To run on a local dev environment, in the root of the repo:


* `./gradlew :api-svc:docker:data-export:dockerBuild`
*
```
docker run --rm --name raido-public-data-export \
  -v C:/ardc/raido-v2/s3/raido-public-data-export/:/raido-public-data-export/export-data \
  -e "API_USER_PASSWORD=xxx" \
  raido-public-data-export:latest
```

I ran the above using git bash - hence the strange windows path with forward 
slashes. 