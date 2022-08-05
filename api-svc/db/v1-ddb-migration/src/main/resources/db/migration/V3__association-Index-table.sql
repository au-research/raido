create table association_index (
  handle text constraint association_index_pkey primary key not null,
  owner_name text not null,
  raid_name text not null,
  role text not null,
  type text not null,
  start_date timestamp without time zone,
  s3_export jsonb not null
);

comment on table association_index is
'imported from arn:aws:dynamodb:ap-southeast-2:005299621378:table/RAiD-RAiDLiveDB-1SX7NYTSOSUKX-AssociationIndexTable-1EMNYHDPK9NBP';

comment on column association_index.type is
'`service|institution` - 14K service, institution only 16';

comment on column association_index.role is
'`owner` - if type is `service`, otherwise not set';

