create table metadata (
  name text constraint metadata_pkey primary key not null,
  type text not null,
  grid text not null,
  isni text not null,
  admin_email text not null,
  tech_email text not null,
  s3_export jsonb not null
);

comment on table metadata is
'from arn:aws:dynamodb:ap-southeast-2:005299621378:table/RAiD-MetadataTable-5X1IHWPICN82';


