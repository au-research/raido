
drop schema if exists :"STASH_SCHEMA_NAME" cascade;
create schema if not exists :"STASH_SCHEMA_NAME";

-- we can't just do create table select from, because then the dependency on the 
-- custom column types (like user_role) means you can't do a pg_restore --clean
-- which is needed, again, because of deps on types :/
-- CREATE TABLE :"STASH_SCHEMA_NAME".app_user AS 
--   SELECT * FROM :"API_SCHEMA_NAME".app_user;

create table :"STASH_SCHEMA_NAME".app_user (
  id               bigint not null,
  service_point_id bigint                                    not null,
  email            varchar(256)                              not null,
  client_id        varchar(256)                              not null,
  subject          varchar(256)                              not null,
  id_provider      varchar(256) not null,
  role             varchar(256) not null,
  enabled          boolean   default true                    not null,
  token_cutoff     timestamp,
  date_created     timestamp default transaction_timestamp() not null
);

-- * prod api-keys won't work because the generated tokens:
--   * generated with different secret
--   * generated with different issuer
-- * won't work if the service point ids don't match between prod and demo
-- either fail insert on FK, or worse, associate the user/api-key with the wrong
-- service point
insert into :"STASH_SCHEMA_NAME".app_user
select * from :"API_SCHEMA_NAME".app_user;
