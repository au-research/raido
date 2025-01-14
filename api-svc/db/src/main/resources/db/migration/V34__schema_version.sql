begin transaction;

alter table api_svc.raid add column schema_version varchar;

update api_svc.raid
set schema_version = '0.0.5'
where metadata_schema = 'raido-metadata-schema-v1';

update api_svc.raid
set schema_version = '1.0.0'
where metadata_schema = 'raido-metadata-schema-v2';

end transaction;