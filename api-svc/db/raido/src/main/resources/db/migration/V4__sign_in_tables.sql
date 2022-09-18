-- create or replace function institution_start_id()
-- returns bigint
-- language sql immutable as
-- $$
-- select 10000000
-- $$;

drop table if exists service_point cascade;
drop table if exists institution cascade;
drop table if exists app_user cascade;
drop table if exists sp_user cascade;
drop table if exists operator cascade;
drop table if exists raido_operator cascade;
drop table if exists auth_request cascade;
drop table if exists authz_request cascade;
drop table if exists user_authz_request cascade;
drop table if exists api_token cascade;
drop table if exists raid cascade;

drop type if exists user_role;
drop type if exists auth_request_status;
drop type if exists id_provider;

--

-- not needed any more
drop table if exists test_table;

create type user_role as enum ('OPERATOR', 'SP_ADMIN', 'SP_USER', 'API');
create type auth_request_status as enum ('REQUESTED', 'APPROVED', 'REJECTED');
create type id_provider as enum ('GOOGLE', 'AAF', 'COGNITO');

create table service_point (
  id             bigint generated always as identity
    (start with 20000000)
    primary key                     not null,
  name           varchar(256)       not null,
  search_content varchar(256)       null,
  admin_email    varchar(256)       not null,
  tech_email     varchar(256)       not null,
  disabled       bool default false not null
);

alter table service_point
  add lower_name text generated always as ( trim(lower(name)) ) stored;
alter table service_point
  add constraint unique_name unique (lower_name);

comment on constraint unique_name on service_point is
  'initially for uniqueness rather than querying, we want to retain the case
  entered by the user';

comment on column service_point.search_content is
  'this can contain content you want to be able to search on, without showing
  it in the name.'
;

alter table service_point
  add constraint lowercase_search_content
    check (search_content = lower(trim(search_content)));
comment on column service_point.search_content is
  'Trimmed lowercase only';


-- 'user' is a reserved word
create table app_user (
  id               bigint generated always as identity
    (start with 1000000000)
    primary key                                         not null,
  service_point_id bigint
    references service_point                            not null,
  email            varchar(256)                         not null,
  client_id        varchar(256)                         not null,
  subject          varchar(256)                         not null,
  id_provider      id_provider                          not null,
  role             user_role                            not null,
  disabled         bool default false                   not null,
  token_cutoff     timestamp without time zone          null,
  date_created     timestamp without time zone
                        default transaction_timestamp() not null
);

-- worried about multple user_request approvals and moving users around between
-- service-points in case of user error, etc.
create unique index app_user_id_fields_active_idx
  on app_user(email, client_id, subject) 
  where disabled = false;

comment on index app_user_id_fields_active_idx is 
'a user can only be attached to one service point at a time.  We could change 
this, but would need to change sign-in to specify service-point';

comment on column app_user.token_cutoff is
  'Any endpoint call with a bearer token issued after this point will be 
  rejected. Any authentication attempt after this point will be rejected.'
;

comment on column app_user.id_provider is
  'not a real identity field, its just redundant info we figure it out from 
  the clientId or issuer and store it for easy analysis'
;

create table raido_operator (
  email varchar(256) not null primary key
);


comment on table raido_operator is
  'any app_user
with an email in this table will be considered an operator';


create table user_authz_request (
  id              bigint generated always as identity
    (start with 30000000)
    primary key                               not null,
  status          auth_request_status         not null,
  serice_point_id bigint not null references service_point,
  email           varchar(256)                not null,
  client_id       varchar(256)                not null,
  id_provider     id_provider                 not null,
  subject         varchar(256)                not null,
  responding_user bigint references app_user  null,
  description     varchar(1024)               not null,
  date_requested  timestamp without time zone default transaction_timestamp(),
  date_responded  timestamp without time zone null
);

alter table user_authz_request
  add constraint lowercase_email_constraint
    check (email = lower(email));
comment on column user_authz_request.email is
  'Lowercase chars only - db enforced';


comment on column user_authz_request.responding_user is
  'not set until approved or rejected';

comment on column user_authz_request.date_responded is
  'not set until approved or rejected';

create table raid (
  handle           varchar(32) primary key                                     not null,
  service_point_id bigint references service_point                             not null,
  content_path     varchar(512)                                                not null,
  content_index    integer                                                     not null,
  name             varchar(256)                                                not null,
  description      varchar(1024)                                               not null,
  dmr              jsonb                                                       not null,
  start_date       timestamp without time zone default transaction_timestamp() not null,
  date_created     timestamp without time zone default transaction_timestamp() not null
);


insert into service_point
  (name, admin_email, tech_email)
values ('raido', 'web.services@ardc.edu.au', 'web.services@ardc.edu.au');

insert into service_point
  (name, search_content, admin_email, tech_email)
values ('Australian Research Data Commons',
        'ardc',
        'matthias.liffers@ardc.edu.au',
        'joel.benn@ardc.edu.au');

insert into raido_operator
values ('shorn.tolley@ardc.edu.au');
insert into raido_operator
values ('matthias.liffers@ardc.edu.au');
insert into raido_operator
values ('shawn.ross@ardc.edu.au');


