When running locally with Docker Compose, the `raid` realm should be added automatically when the container starts.

The realm includes 2 test users `raid-test-user` and `uq-test-user`, each belonging to different service points. They 
both have the `service-point-user` role necessary to mint RAiDs. Both have the password 'password'. 

If you make any changes to the realm and want to save them to the configuration, run the command...
```
docker exec -it 74de075c7a49 bin/kc.sh export --realm raid --file /opt/keycloak/data/import/local-raid-realm.json
```
and commit the changes.