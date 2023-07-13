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
    uri      varchar not null,
    primary key (scheme_id, uri),
    constraint fk_title_type_scheme_id foreign key (scheme_id) references raido.api_svc.title_type_scheme (id)
);

insert into raido.api_svc.title_type_scheme (uri)
values ('https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1');

insert into raido.api_svc.title_type (scheme_id, uri) values
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
    uri      varchar not null,
    primary key (scheme_id, uri),
    constraint fk_description_type_scheme_id foreign key (scheme_id) references raido.api_svc.description_type_scheme (id)
);

insert into raido.api_svc.description_type_scheme (uri) values
    ('https://github.com/au-research/raid-metadata/tree/main/scheme/description/type/v1');

insert into raido.api_svc.description_type (scheme_id, uri) values
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
    uri      varchar not null,
    primary key (scheme_id, uri),
    constraint fk_access_type_scheme_id foreign key (scheme_id) references raido.api_svc.access_type_scheme (id)
);

insert into raido.api_svc.access_type_scheme (uri) values
    ('https://github.com/au-research/raid-metadata/tree/main/scheme/access/type/v1');

insert into raido.api_svc.access_type (scheme_id, uri) values
    (1, 'https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/closed.json'),
    (1, 'https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/open.json');


drop table if exists raido.api_svc.contributor_position_type_scheme;
create table raido.api_svc.contributor_position_type_scheme (
    id serial primary key,
    uri varchar not null
);

drop table if exists raido.api_svc.contributor_position_type;
create table raido.api_svc.contributor_position_type
(
    scheme_id int     not null,
    uri      varchar not null,
    primary key (scheme_id, uri),
    constraint fk_access_type_scheme_id foreign key (scheme_id) references raido.api_svc.access_type_scheme (id)
);

insert into raido.api_svc.contributor_position_type_scheme (uri) values
    ('https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/position/v1');

insert into raido.api_svc.access_type (scheme_id, uri) values
    (1, 'https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/co-investigator.json'),
    (1, 'https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/contact-person.json'),
    (1, 'https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/leader.json'),
    (1, 'https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/other-participant.json'),
    (1, 'https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/principal-investigator.json');

drop table if exists raido.api_svc.contributor_role_type_scheme;
create table raido.api_svc.contributor_role_type_scheme (
    id serial primary key,
    uri varchar not null
);

drop table if exists raido.api_svc.contributor_role_type;
create table raido.api_svc.contributor_role_type
(
    scheme_id int     not null,
    uri      varchar not null,
    primary key (scheme_id, uri),
    constraint fk_access_type_scheme_id foreign key (scheme_id) references raido.api_svc.access_type_scheme (id)
);

insert into raido.api_svc.contributor_role_type_scheme (uri) values
    ('https://credit.niso.org');

insert into raido.api_svc.access_type (scheme_id, uri) values
    (1, 'https://credit.niso.org/contributor-roles/conceptualization/'),
    (1, 'https://credit.niso.org/contributor-roles/data-curation/'),
    (1, 'https://credit.niso.org/contributor-roles/formal-analysis/'),
    (1, 'https://credit.niso.org/contributor-roles/funding-acquisition/'),
    (1, 'https://credit.niso.org/contributor-roles/investigation/'),
    (1, 'https://credit.niso.org/contributor-roles/methodology/'),
    (1, 'https://credit.niso.org/contributor-roles/project-administration/'),
    (1, 'https://credit.niso.org/contributor-roles/resources/'),
    (1, 'https://credit.niso.org/contributor-roles/software/'),
    (1, 'https://credit.niso.org/contributor-roles/supervision/'),
    (1, 'https://credit.niso.org/contributor-roles/validation/'),
    (1, 'https://credit.niso.org/contributor-roles/visualization/'),
    (1, 'https://credit.niso.org/contributor-roles/writing-original-draft/'),
    (1, 'https://credit.niso.org/contributor-roles/writing-review-editing/');

end transaction;