begin transaction;

drop table if exists raido.api_svc.title_type_scheme;
create table raido.api_svc.title_type_scheme (
   id serial primary key,
   uri varchar not null
);

drop table if exists raido.api_svc.title_type;
create table raido.api_svc.title_type
(
    scheme_id int     not null,
    name      varchar not null,
    primary key (scheme_id, name),
    constraint fk_title_type_scheme_id foreign key (scheme_id) references raido.api_svc.title_type_scheme (id)
);

insert into raido.api_svc.title_type_scheme (uri)
values ('https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1');

insert into raido.api_svc.title_type (scheme_id, name) values
   (1, 'https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/alternative.json'),
   (1, 'https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/primary.json');


drop table if exists raido.api_svc.description_type_scheme;
create table raido.api_svc.description_type_scheme (
    id serial primary key,
    uri varchar not null
);

drop table if exists raido.api_svc.description_type;
create table raido.api_svc.description_type
(
    scheme_id int     not null,
    name      varchar not null,
    primary key (scheme_id, name),
    constraint fk_description_type_scheme_id foreign key (scheme_id) references raido.api_svc.description_type_scheme (id)
);

insert into raido.api_svc.description_type_scheme (uri) values
    ('https://github.com/au-research/raid-metadata/tree/main/scheme/description/type/v1');

insert into raido.api_svc.description_type (scheme_id, name) values
    (1, 'https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v1/alternative.json'),
    (1, 'https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v1/primary.json');

drop table if exists raido.api_svc.access_type_scheme;
create table raido.api_svc.access_type_scheme (
    id serial primary key,
    uri varchar not null
);

drop table if exists raido.api_svc.access_type;
create table raido.api_svc.access_type
(
    scheme_id int     not null,
    name      varchar not null,
    primary key (scheme_id, name),
    constraint fk_access_type_scheme_id foreign key (scheme_id) references raido.api_svc.access_type_scheme (id)
);

insert into raido.api_svc.access_type_scheme (uri) values
    ('https://github.com/au-research/raid-metadata/tree/main/scheme/access/type/v1');

insert into raido.api_svc.access_type (scheme_id, name) values
    (1, 'https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/closed.json'),
    (1, 'https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/open.json');


end transaction;