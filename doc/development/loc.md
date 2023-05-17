
2022-09-06 is a nice "start point", so I thought I'd record LoC at this 
point.

All the bits are there: DB, api-svc, app-client.
Front and backends are talking together via OpenAPI, and users can 
login.  The core of the front-end infrastructure is in place.

But we haven't even created any DB schema yet at this point (except the v1 
legacy import).


## 2022-09-06

```shell
find -name '*.tsx' | grep -v 'node_modules' | xargs wc -l
find -name '*.ts' | grep -v 'node_modules' | xargs wc -l
find -name '*.md' | grep -v 'node_modules' | xargs wc -l
find -name '*.sql' | grep -v 'node_modules' | xargs wc -l
find -name '*.java' | grep -v 'node_modules' | xargs wc -l
find -name '*.groovy' | grep -v 'node_modules' | xargs wc -l
find -name '*.gradle' | grep -v 'node_modules' | xargs wc -l
find -name '*.yaml' | grep -v 'node_modules' | xargs wc -l
```

| type   | lines  |
|--------|--------|
| tsx    | 2,755  |
| ts     | 1,607  |
| md     | 2,386  |
| sql    | 483    |
| java   | 16,392 |
| groovy | 1,007  |
| gradle | 1,207  |
| yaml   | 1,721  |


## 2023-03-21

Just after 1.0 Raido rescue deployment.

Not sure if original count was done on a clean codebase.
These counts were taken after doing `gradlew clean`

| type   | lines  |
|--------|--------|
| tsx    | 7,055  |
| ts     | 1,215  |
| md     | 4,156  |
| sql    | 1,092  |
| java   | 28,941 |
| groovy | 1,332  |
| gradle | 1,469  |
| yaml   | 3,113  |
