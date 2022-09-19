-- user and role are synonyms in PG 

-- because I couldn't get the "drop user" funtionality workin in RDS, now 
-- this whole script is "conditional" 
-- https://flywaydb.org/documentation/learnmore/faq.html#db-specific-sql
-- this script should only be run the very first time, after a DB was created

do
$$begin
  -- can't drop the user if it has privileges, but neither drop nor revoke support
  -- "if exists" type functionality
  if exists (select from pg_roles where rolname = 'api_user') then
    -- In RDS, this must be run as superuser, but only `rdsadmin` is superuser
    -- and I couldn't figure out how to execute as that user from codebuild.
    -- the "drop user" stuff is left in to make running in local dev against
    -- a docker container easier
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
  -- this is the user that will be used to connect as, in order to avoid
  -- embedding credentials in source, it is passed in as a placeholder.
  login password '${apiSvcRolePassword}'
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

