-- user and role are synonyms in PG 

-- can't drop the user if it has privileges, but neither drop nor revoke support
-- "if exists" type functionality
do
$$begin
  if exists (select from pg_roles where rolname = 'api_user') then
    execute 'drop owned by api_user';
  end if;
end$$;

-- flyway clean does not drop the user
drop role if exists api_user;

create role api_user
  nosuperuser
  nocreatedb
  nocreaterole
  noreplication 
  connection limit -1
  nobypassrls
  -- this is the user that will be used to connect as, but in order to avoid
  -- putting the password here, we set it to null and it must be set manually
  -- the api-svc will not be able to connect until this is done.
  -- `FATAL: password authentication failed for user "api_user"`
  -- to change the password: `alter user api_user password 'wobble'`
  login password null
;

comment on role api_user is 
'the role that the api-svc connects as to issue queries';

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

