grant connect on database raido to api_user;

grant usage on schema api_svc to api_user;
grant usage on schema raid_v1_import to api_user;

grant all privileges on all tables in schema api_svc to api_user;
grant all privileges on all tables in schema raid_v1_import to api_user;

alter default privileges in schema api_svc
  grant all on tables to api_user;
alter default privileges in schema raid_v1_import
  grant all on tables to api_user;

grant usage on all sequences in schema api_svc to api_user;
grant usage on all sequences in schema raid_v1_import to api_user;

alter default privileges in schema api_svc 
  grant usage on sequences to api_user;
alter default privileges in schema raid_v1_import 
  grant usage on sequences to api_user;

grant execute on all routines in schema api_svc to api_user;
grant execute on all routines in schema raid_v1_import to api_user;

