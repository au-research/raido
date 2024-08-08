#!/bin/bash

set -e

# Wait for the RAiD API to be ready
until curl -sSf http://raid-api:8080/actuator/health; do
  echo "Waiting for application..."
  sleep 5
done

echo "Running task..."

# Request access token
response=$(curl --location 'http://raid-iam:8080/realms/raid/protocol/openid-connect/token' \
--data-urlencode 'client_id=raid-api' \
--data-urlencode 'username=raid-operator' \
--data-urlencode 'password=password' \
--data-urlencode 'grant_type=password')

# Extract the access token from the response
access_token=$(echo $response | jq -r .access_token)

# Update Raid AU service point
curl --location --request PUT 'raid-api:8080/service-point/20000000' \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer $access_token" \
--data-raw '{
    "id": 20000000,
    "name": "raido",
    "identifierOwner": "https://ror.org/038sjwq14",
    "techEmail": "web.services@ardc.edu.au",
    "adminEmail": "web.services@ardc.edu.au",
    "enabled": true,
    "appWritesEnabled": true,
    "repositoryId": "datacite-username",
    "prefix": "10.82841",
    "password": "datacite-password",
    "groupId": "169bd3f3-dd42-4ac0-b89a-fb49648e5eff"
}'

curl --location --request PUT 'raid-api:8080/service-point/20000002' \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer $access_token" \
--data-raw '{
    "id": 20000002,
    "name": "RDM@UQ",
    "identifierOwner": "https://ror.org/00rqy9422",
    "techEmail": "",
    "adminEmail": "",
    "enabled": true,
    "appWritesEnabled": true,
    "repositoryId": "datacite-username",
    "prefix": "10.82841",
    "password": "datacite-password",
    "groupId": "ba0b01a6-726f-464f-b501-454a10096826"
}'

echo "Task completed!"
