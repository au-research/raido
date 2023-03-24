-- create schema :API_SCHEMA_NAME authorization :API_USER_NAME;

begin;

set local raido.API_USER_NAME = :'API_USER_NAME';
set local raido.API_SCHEMA_NAME = :'API_SCHEMA_NAME';

do
$$
  declare
    apiUserName   text := current_setting('raido.API_USER_NAME');
    apiSchemaName text := current_setting('raido.API_SCHEMA_NAME');
  begin

    if not exists(
      select schema_name
      from information_schema.schemata
      where schema_name = apiSchemaName)
    then
      execute format(
        'CREATE SCHEMA %I AUTHORIZATION %I',
        apiSchemaName, apiUserName);
    end if;
  end
$$;

end;

