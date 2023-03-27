begin;

SET LOCAL raido.PG_ADMIN_USER = :'PG_ADMIN_USER';
SET LOCAL raido.API_USER_NAME = :'API_USER_NAME';
SET LOCAL raido.API_USER_PASSWORD = :'API_USER_PASSWORD';

do
$$
  declare
    pgAdminUser text := current_setting('raido.PG_ADMIN_USER');
    apiUserName text := current_setting('raido.API_USER_NAME');
    apiUserPassword text := current_setting('raido.API_USER_PASSWORD');
  begin
    
    if not exists(
      select from pg_catalog.pg_roles where rolname = apiUserName
    ) then
      EXECUTE FORMAT(
        'CREATE ROLE %I NOSUPERUSER NOCREATEDB NOCREATEROLE LOGIN', 
        apiUserName );
    end if;

    EXECUTE FORMAT(
      'ALTER ROLE %I WITH PASSWORD %L', 
      apiUserName, apiUserPassword );

    EXECUTE FORMAT('GRANT %I TO %I', apiUserName, pgAdminUser);
  end
$$;

end;

