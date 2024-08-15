#!/bin/bash

set -e

export PGDATABASE=raido
export PGHOST=raid-api-db
export PGPORT=5432
export PGUSER=postgres
export PGPASSWORD=supersecret

#until pg_isready -d $PGDATABASE -h $PGHOST -p $PGPORT -U $PGUSER -  ; do
until pg_isready; do
  echo "Waiting for ${PGHOST}..."
  sleep 5
done

echo "Update service points..."

psql -c "update raido.api_svc.service_point set prefix = '10.82841', password = 'datacite-password', repository_id = 'datacite-username', group_id = 'ba0b01a6-726f-464f-b501-454a10096826' where id = 20000002"
psql -c "update raido.api_svc.service_point set prefix = '10.82841', password = 'datacite-password', repository_id = 'datacite-username', group_id = '169bd3f3-dd42-4ac0-b89a-fb49648e5eff' where id = 20000000"

echo "Task completed!"
