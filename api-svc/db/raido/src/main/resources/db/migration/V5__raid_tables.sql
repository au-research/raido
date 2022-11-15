drop table if exists raid cascade;
drop table if exists raid_v2 cascade;

drop type if exists metaschema;

create type metaschema as enum ('raido-metadata-schema-v1');

create table raid (
  handle           varchar(32) primary key                                     not null,
  service_point_id bigint references service_point                             not null,
  url              varchar(512)                                                not null,
  url_index        integer                                                     not null,
  primary_title    varchar(256)                                                not null,
  confidential     boolean                                                     not null,
  metadata_schema  metaschema                                                  not null,
  metadata         jsonb                                                       not null,
  start_date       date                        default transaction_timestamp() not null,
  date_created     timestamp without time zone default transaction_timestamp() not null
);

comment on column raid.url is
  'The value that we set as the `URL` property via ARDC APIDS.
  Example: `https://demo.raido-infra.com/raid/123.456/789`. 
  The global handle regisrty url (e.g. `https://hdl.handle.net/123.456/789`) 
  will redirect to this value.';

comment on column raid.url_index is
  'The `index` of the URL property in APIDS. This can be different if we change
  how we mint URL values via APIDS.';

comment on column raid.metadata_schema is
  'Identifies the structure of the data in the metadata column';

