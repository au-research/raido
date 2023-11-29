-- TITLE

create table api_svc.title_type_new
(
    id        serial primary key,
    uri       varchar not null,
    schema_id int     not null
);

insert into api_svc.title_type_new (uri, schema_id)
select uri, schema_id
from api_svc.title_type;

drop table api_svc.title_type;
alter table api_svc.title_type_new rename to title_type;

create table api_svc.title
(
    id        serial primary key,
    raid_name varchar not null,
    title_type_id      int     not null,
    value     text,
    constraint fk_title_raid_name foreign key (raid_name) references api_svc.raid(handle),
    constraint fk_title_type foreign key (title_type_id) references api_svc.title_type(id)
);
-- /TITLE

-- DESCRIPTION
create table api_svc.description_type_new
(
    id        serial primary key,
    uri       varchar not null,
    schema_id int     not null
);

insert into api_svc.description_type_new (uri, schema_id)
select uri, schema_id
from api_svc.description_type;

drop table api_svc.description_type;
alter table api_svc.description_type_new rename to description_type;

create table api_svc.description
(
    id        serial primary key,
    raid_name varchar not null,
    description_type_id      int     not null,
    value     text,
    constraint fk_description_raid_name foreign key (raid_name) references api_svc.raid(handle),
    constraint fk_description_type foreign key (description_type_id) references api_svc.description_type(id)
);

-- /DESCRIPTION

-- CONTRIBUTOR

create table api_svc.contributor_position_new
(
    id        serial primary key,
    uri       varchar not null,
    schema_id int     not null
);

insert into api_svc.contributor_position_new (uri, schema_id)
select uri, schema_id
from api_svc.contributor_position;

drop table api_svc.contributor_position;
alter table api_svc.contributor_position_new rename to contributor_position;

create table api_svc.contributor_role_new
(
    id        serial primary key,
    uri       varchar not null,
    schema_id int     not null
);

insert into api_svc.contributor_role_new (uri, schema_id)
select uri, schema_id
from api_svc.contributor_role;

drop table api_svc.contributor_role;
alter table api_svc.contributor_role_new rename to contributor_role;

create table api_svc.contributor_schema
(
    id         int primary key,
    schema_uri varchar not null
);

create table api_svc.contributor
(
    id        serial primary key,
    pid       varchar not null,
    schema_id int     not null,
    constraint fk_contributor_schema_id foreign key (schema_id) references contributor_schema (id)
);

create table api_svc.raid_contributor_position
(
    raid_name               varchar not null,
    contributor_id          int     not null,
    contributor_position_id int     not null,
    primary key (raid_name, contributor_id, contributor_position_id),
    constraint fk_raid_contributor_position_raid_name foreign key (raid_name) references api_svc.raid (handle),
    constraint fk_raid_contributor_contributor_id foreign key (contributor_id) references api_svc.contributor (id),
    constraint fk_raid_contributor_position_contributor_position_id foreign key (contributor_position_id) references api_svc.contributor_position (id)
);

create table api_svc.raid_contributor_role
(
    raid_name           varchar not null,
    contributor_id      int     not null,
    contributor_role_id int     not null,
    primary key (raid_name, contributor_id, contributor_role_id),
    constraint fk_raid_contributor_role_raid_name foreign key (raid_name) references api_svc.raid (handle),
    constraint fk_raid_contributor_contributor_id foreign key (contributor_id) references api_svc.contributor (id),
    constraint fk_raid_contributor_role_contributor_role_id foreign key (contributor_role_id) references api_svc.contributor_role (id)
);

-- /CONTRIBUTOR

-- ORGANISATION

create table api_svc.organisation_schema
(
    id         int primary key,
    schema_uri varchar not null
);

create table api_svc.organisation
(
    id        serial primary key,
    pid       varchar not null,
    schema_id int     not null,
    constraint fk_organisation_schema_id foreign key (schema_id) references organisation_schema (id)
);

create table api_svc.organisation_role_new
(
    id        serial primary key,
    uri       varchar not null,
    schema_id int     not null
);

insert into api_svc.organisation_role_new (uri, schema_id)
select uri, schema_id
from api_svc.organisation_role;

drop table api_svc.organisation_role;
alter table api_svc.organisation_role_new rename to organisation_role;

create table api_svc.raid_organisation_role
(
    raid_name            varchar not null,
    organisation_id      int     not null,
    organisation_role_id int     not null,
    primary key (raid_name, organisation_id, organisation_role_id),
    constraint fk_raid_organisation_role_raid_name foreign key (raid_name) references api_svc.raid (handle),
    constraint fk_raid_organisation_role_organisation_id foreign key (organisation_id) references api_svc.organisation (id),
    constraint fk_raid_organisation_role_organisation_role_id foreign key (organisation_role_id) references api_svc.organisation_role (id)
);

-- /ORGANISATION

-- RELATED OBJECT

create table api_svc.related_object_category_new
(
    id        serial primary key,
    uri       varchar not null,
    schema_id int     not null
);

insert into api_svc.related_object_category_new (uri, schema_id)
select uri, schema_id
from api_svc.related_object_category;

drop table api_svc.related_object_category;
alter table api_svc.related_object_category_new rename to related_object_category;

create table api_svc.related_object_type_new
(
    id        serial primary key,
    uri       varchar not null,
    schema_id int     not null
);

insert into api_svc.related_object_type_new (uri, schema_id)
select uri, schema_id
from api_svc.related_object_type;

drop table api_svc.related_object_type;
alter table api_svc.related_object_type_new rename to related_object_type;

create table api_svc.related_object
(
    id serial primary key,
    pid         varchar not null,
    schema_uri varchar not null
);

create table api_svc.raid_related_object
(
    raid_name                  varchar not null,
    related_object_id          int     not null,
    related_object_category_id int     not null,
    related_object_type_id     int     not null,
    constraint fk_raid_related_object_related_object_id foreign key (related_object_id) references api_svc.related_object (id),
    constraint fk_raid_related_object_related_object_category_id foreign key (related_object_category_id) references api_svc.related_object_category (id),
    constraint fk_raid_related_object_related_object_type_id foreign key (related_object_type_id) references api_svc.related_object_type (id)
);

-- /RELATED OBJECT

-- ALTERNATE IDENTIFIER

create table api_svc.raid_alternate_identifier
(
    raid_name varchar not null,
    id        varchar not null,
    type      varchar not null,
    primary key (raid_name, id, type),
    constraint fk_raid_alternate_identifier_raid_name foreign key (raid_name) references api_svc.raid (handle)
);

-- /ALTERNATE IDENTIFIER

-- ALTERNATE URL
create table api_svc.raid_alternate_url
(
    raid_name varchar not null,
    url       varchar not null,
    primary key (raid_name, url),
    constraint fk_raid_alternate_url_raid_name foreign key (raid_name) references api_svc.raid (handle)
);

-- /ALTERNATE URL

-- RELATED RAID
create table api_svc.related_raid_type_new
(
    id        serial primary key,
    uri       varchar not null,
    schema_id int     not null
);

insert into api_svc.related_raid_type_new (uri, schema_id)
select uri, schema_id
from api_svc.related_raid_type;

drop table api_svc.related_raid_type;
alter table api_svc.related_raid_type_new rename to related_raid_type;

create table api_svc.related_raid
(
    raid_name            varchar not null,
    related_raid_name    varchar not null,
    related_raid_type_id int     not null,
    primary key (raid_name, related_raid_name),
    constraint fk_related_raid_raid_name foreign key (raid_name) references api_svc.raid (handle)
);

-- /RELATED RAID

-- RAID

alter table api_svc.raid
    add column end_date            varchar,
    add column registration_agency varchar,
    add column owner               varchar,
    add column license             varchar,
    add column access_type_id      int,
    add column embargo_expiry      varchar,
    add column access_statement    text,
    drop column url_index,
    drop column primary_title;

-- /RAID

-- SUBJECT

create table api_svc.raid_subject_type
(
    raid_name  varchar not null,
    subject_type_id varchar not null,
    keyword    varchar,
    primary key (raid_name, subject_type_id),
    constraint fk_raid_subject_type_raid_name foreign key (raid_name) references api_svc.raid (handle),
    constraint fk_raid_subject_type_subject_id foreign key (subject_type_id) references api_svc.subject_type (id)
);

-- /SUBJECT

-- TRADITIONAL KNOWLEDGE LABEL

create table api_svc.traditional_knowledge_label_new
(
    id        serial primary key,
    uri       varchar not null,
    schema_id int     not null
);

insert into api_svc.traditional_knowledge_label_new (uri, schema_id)
select uri, schema_id
from api_svc.traditional_knowledge_label;

drop table api_svc.traditional_knowledge_label;
alter table api_svc.traditional_knowledge_label_new rename to traditional_knowledge_label;

create table api_svc.raid_traditional_knowledge_label
(
    raid_name                      varchar not null,
    traditional_knowledge_label_id int     not null,
    primary key (raid_name, traditional_knowledge_label_id),
    constraint fk_raid_traditional_knowledge_label_raid_name foreign key (raid_name) references api_svc.raid (handle)
);

-- /TRADITIONAL KNOWLEDGE LABEL

-- SPATIAL COVERAGE

create table api_svc.spatial_coverage_schema
(
    id         serial primary key,
    schema_uri varchar not null
);

create table api_svc.raid_spatial_coverage
(
    raid_name varchar not null,
    id        varchar not null,
    schema_id int     not null,
    primary key (raid_name, id, schema_id),
    constraint fk_raid_spatial_coverage_raid_name foreign key (raid_name) references api_svc.raid (handle),
    constraint fk_raid_spatial_coverage_schema_id foreign key (schema_id) references api_svc.spatial_coverage_schema (id)
);

-- /SPATIAL COVERAGE

