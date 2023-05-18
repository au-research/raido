ALTER TYPE raido.api_svc.metaschema ADD VALUE IF NOT EXISTS 'raido-metadata-schema-v2';

alter table raido.api_svc.raid add column version int default 1;

-- Update existing metadata to add version to id block with value of 1
update raido.api_svc.raid r set metadata = (select jsonb_set(to_jsonb(metadata), '{id, version}', '1', true) from raido.api_svc.raid where handle = r.handle)

