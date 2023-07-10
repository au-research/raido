
To run on a local dev environment, in the root of the repo:


* `./gradlew :api-svc:docker:data-export:dockerBuild`
*
```
docker run --rm --name raido-public-data-export \
  -v C:/ardc/raido-v2/s3/raido-public-data-export/:/raido-public-data-export/export-data \
  -e "API_USER_PASSWORD=xxx" \
  raido-public-data-export:latest
```