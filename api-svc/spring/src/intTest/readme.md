
### Running funtional tests

At the moment, you need to set a pre-existing token.

`~/.config.raid-v1/api-svc-inttest.properties`:
```properties
raidoArdcLiveToken=xxx.yyy.zzz
```

IMPROVE:STO
Would be better to override the Raido jwtSecret itself in 
[inttest.properties](./java/inttest.properties) and create tokens
(both sign them and insert into the `token` table).

