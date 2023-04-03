
begin;

set local raido.OWNER_NAME = :'OWNER_NAME';
set local raido.CREATE_SCHEMA_NAME = :'CREATE_SCHEMA_NAME';

do
$$
  declare
    ownerName   text := current_setting('raido.OWNER_NAME');
    schemaName text := current_setting('raido.CREATE_SCHEMA_NAME');
  begin

    if not exists(
      select schema_name
      from information_schema.schemata
      where schema_name = schemaName)
    then
      execute format(
        'CREATE SCHEMA %I AUTHORIZATION %I',
        schemaName, ownerName);
    end if;
  end
$$;

end;

