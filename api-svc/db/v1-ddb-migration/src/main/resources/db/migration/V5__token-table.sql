create table token (
  name text not null,
  environment text not null,
  date_created timestamp without time zone,
  token text not null,
  s3_export jsonb not null,
  primary key (name, environment, date_created)
);

comment on table token is
'from arn:aws:dynamodb:ap-southeast-2:005299621378:table/RAiD-TokenTable-1P6MFZ0WFEETH';


