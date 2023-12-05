BEGIN TRANSACTION;

create table api_svc.language_new
(
    id        serial primary key,
    code      varchar not null,
    name      varchar not null,
    schema_id int     not null
);


insert into api_svc.language_new (code, name, schema_id)
select id, name, schema_id
from api_svc.language;

drop table api_svc.language;
alter table api_svc.language_new rename to language;

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
    id            serial primary key,
    raid_name     varchar not null,
    title_type_id int     not null,
    value         text    not null,
    language_id   int,
    start_date varchar not null,
    end_date varchar,
    constraint fk_title_raid_name foreign key (raid_name) references api_svc.raid (handle),
    constraint fk_title_type foreign key (title_type_id) references api_svc.title_type (id),
    constraint fk_title_language_id foreign key (language_id) references api_svc.language (id)
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
    id                  serial primary key,
    raid_name           varchar not null,
    description_type_id int     not null,
    value               text    not null,
    language_id         int,
    constraint fk_description_raid_name foreign key (raid_name) references api_svc.raid (handle),
    constraint fk_description_type foreign key (description_type_id) references api_svc.description_type (id),
    constraint fk_description_language_id foreign key (language_id) references api_svc.language (id)
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
    id         serial primary key,
    uri varchar not null
);

insert into api_svc.contributor_schema (uri)
values ('https://orcid.org/');




create table api_svc.contributor
(
    id        serial primary key,
    pid       varchar not null,
    schema_id int     not null,
    unique(pid, schema_id),
    constraint fk_contributor_schema_id foreign key (schema_id) references contributor_schema (id)
);

create table api_svc.raid_contributor
(
    id             serial primary key,
    raid_name      varchar not null,
    contributor_id int     not null,
    leader         boolean,
    contact        boolean,
    unique (raid_name, contributor_id),
    constraint fk_raid_contributor_raid_name foreign key (raid_name) references api_svc.raid (handle),
    constraint fk_raid_contributor_contributor_id foreign key (contributor_id) references api_svc.contributor (id)
);

create table api_svc.raid_contributor_position
(
    id                      serial primary key,
    raid_contributor_id     int     not null,
    contributor_position_id int     not null,
    start_date              varchar not null,
    end_date                varchar,
    unique(raid_contributor_id, contributor_position_id, start_date, end_date),
    constraint fk_raid_contributor_position_raid_contributor_id foreign key (raid_contributor_id) references api_svc.raid_contributor (id),
    constraint fk_raid_contributor_position_contributor_position_id foreign key (contributor_position_id) references api_svc.contributor_position (id)
);

create table api_svc.raid_contributor_role
(
    id                  serial primary key,
    raid_contributor_id int not null,
    contributor_role_id int not null,
    unique(raid_contributor_id, contributor_role_id),
    constraint fk_raid_contributor_role_raid_contributor_id foreign key (raid_contributor_id) references api_svc.raid_contributor (id),
    constraint fk_raid_contributor_role_contributor_role_id foreign key (contributor_role_id) references api_svc.contributor_role (id)
);

-- /CONTRIBUTOR

-- ORGANISATION

create table api_svc.organisation_schema
(
    id  serial primary key,
    uri varchar not null,
    unique(uri)
);

insert into api_svc.organisation_schema (uri)
values ('https://ror.org/');


create table api_svc.organisation
(
    id        serial primary key,
    pid       varchar not null,
    schema_id int     not null,
    unique(pid, schema_id),
    constraint fk_organisation_schema_id foreign key (schema_id) references organisation_schema (id)
);

create table api_svc.organisation_role_new
(
    id        serial primary key,
    uri       varchar not null,
    schema_id int     not null,
    unique (uri, schema_id)
);

insert into api_svc.organisation_role_new (uri, schema_id)
select uri, schema_id
from api_svc.organisation_role;

drop table api_svc.organisation_role;
alter table api_svc.organisation_role_new rename to organisation_role;


create table api_svc.raid_organisation
(
    id              serial primary key,
    raid_name       varchar not null,
    organisation_id int     not null,
    unique(raid_name, organisation_id)
);

create table api_svc.raid_organisation_role
(
    id                   serial primary key,
    raid_organisation_id int not null,
    organisation_role_id int not null,
    start_date           varchar,
    end_date             varchar,
    unique(raid_organisation_id, organisation_role_id, start_date, end_date),
    constraint fk_raid_organisation_role_raid_organisation_id foreign key (raid_organisation_id) references api_svc.raid_organisation (id),
    constraint fk_raid_organisation_role_organisation_role_id foreign key (organisation_role_id) references api_svc.organisation_role (id)
);


-- /ORGANISATION

-- RELATED OBJECT

create table api_svc.related_object_schema
(
    id  serial primary key,
    uri varchar not null,
    unique (uri)
);
insert into api_svc.related_object_schema (uri)
values ('https://doi.org/');


create table api_svc.related_object_category_new
(
    id        serial primary key,
    uri       varchar not null,
    schema_id int     not null,
    unique(uri, schema_id)
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
    schema_id int     not null,
    unique(uri, schema_id)
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
    schema_uri varchar not null,
    unique(pid, schema_uri)
);

create table api_svc.raid_related_object
(
    id                         serial primary key,
    raid_name                  varchar not null,
    related_object_id          int     not null,
    related_object_type_id     int     not null,
    unique (raid_name, related_object_id, related_object_type_id),
    constraint fk_raid_related_object_raid_name foreign key (raid_name) references api_svc.raid (handle),
    constraint fk_raid_related_object_related_object_id foreign key (related_object_id) references api_svc.related_object (id),
    constraint fk_raid_related_object_related_object_type_id foreign key (related_object_type_id) references api_svc.related_object_type (id)
);

create table api_svc.raid_related_object_category
(
    id                         serial primary key,
    raid_related_object_id     int not null,
    related_object_category_id int not null,
    unique(raid_related_object_id, related_object_category_id),
    constraint fk_raid_related_object_category_raid_related_object_id foreign key (raid_related_object_id) references api_svc.raid_related_object (id),
    constraint fk_raid_related_object_category_related_object_category_id foreign key (related_object_category_id) references api_svc.related_object_category (id)
);

insert into api_svc.raid_related_object_category (raid_related_object_id, related_object_category_id)
select
    (select rro.id from api_svc.raid_related_object rro join api_svc.related_object ro on rro.related_object_id = ro.id where rro.raid_name = r.handle and ro.pid = r.related_object_id) as raid_related_object_id,
    (select id from api_svc.related_object_category where uri = related_object_category_id) as related_object_category_id
from (select handle, x.id as related_object_id, xx.id as related_object_category_id
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{relatedObject}') as x(id text, "schemaUri" text, type jsonb, category jsonb),
           lateral jsonb_to_record(category) as xx(id text, "schemaUri" text)
      where api_svc.raid.metadata_schema = 'raido-metadata-schema-v2' and metadata ->> 'relatedObject' is not null) r
on conflict do nothing;


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
    schema_id int     not null,
    unique (uri, schema_id)
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
    add column access_statement_language_id int,
    add constraint fk_raid_access_statement_language_id foreign key (access_statement_language_id) references language (id),
    alter column url_index drop not null,
    alter column primary_title drop not null;
;


-- /RAID

-- SUBJECT

create table api_svc.raid_subject
(
    id                  serial primary key,
    raid_name           varchar not null,
    subject_type_id     varchar not null,
    unique (raid_name, subject_type_id),
    constraint fk_raid_subject_raid_name foreign key (raid_name) references api_svc.raid (handle),
    constraint fk_raid_subject_subject_id foreign key (subject_type_id) references api_svc.subject_type (id)
);

create table api_svc.raid_subject_keyword
(
    id              serial primary key,
    raid_subject_id int not null,
    keyword         varchar not null,
    language_id     int,
    unique (raid_subject_id, keyword, language_id),
    constraint fk_raid_subject_keyword_raid_subject_id foreign key (raid_subject_id) references api_svc.raid_subject (id),
    constraint fk_raid_subject_keyword_language_id foreign key (language_id) references api_svc.language (id)
);




-- /SUBJECT

-- TRADITIONAL KNOWLEDGE LABEL

create table api_svc.traditional_knowledge_label_new
(
    id        serial primary key,
    uri       varchar not null,
    schema_id int     not null,
    unique (uri, schema_id)
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
    schema_uri varchar not null,
    unique (schema_uri)
);

create table api_svc.raid_spatial_coverage
(
    raid_name varchar not null,
    id        varchar not null,
    schema_id int     not null,
    place varchar,
    place_language_id int,
    primary key (raid_name, id, schema_id),
    constraint fk_raid_spatial_coverage_raid_name foreign key (raid_name) references api_svc.raid (handle),
    constraint fk_raid_spatial_coverage_schema_id foreign key (schema_id) references api_svc.spatial_coverage_schema (id),
    constraint fk_raid_spatial_coverage_place_language_id foreign key (place_language_id) references api_svc.language (id)
);

-- /SPATIAL COVERAGE

-- INSERT TITLES
-- METADATA SCHEMA V2
insert into api_svc.title (raid_name, title_type_id, value, language_id, start_date, end_date)
select r.handle as raid_name,
       (select id from api_svc.title_type where uri = r.type::json->> 'id') as title_type_id,
       r.text as value,
       (select id from api_svc.language where code = r.language::json->> 'id') as language_id,
       "startDate" as start_date,
       "endDate" as end_date
from (select handle, x.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{title}') as x(type text, "text" text, "startDate" text, "endDate" text, language text)
      where api_svc.raid.metadata_schema = 'raido-metadata-schema-v2') as r
on conflict do nothing;


-- METADATA SCHEMA V2 & LEGACY
insert into api_svc.title (raid_name, title_type_id, value, start_date, end_date)
select r.handle as raid_name,
       case when r.type = 'Primary Title' then 2
            else 1 end as title_type_id,
       r.title as value,
       "startDate" as start_date,
       "endDate" as end_date
from (select handle, x.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{titles}') as x(type text, "title" text, "startDate" date, "endDate" date)
      where api_svc.raid.metadata_schema <> 'raido-metadata-schema-v2') as r
on conflict do nothing;

-- /INSERT TITLES

-- INSERT DESCRIPTIONS

insert into api_svc.description (raid_name, description_type_id, value, language_id)
select r.handle as raid_name,
       (select id from api_svc.description_type where uri = r.type::json->> 'id') as description_type_id,
       r.text as value,
       (select id from api_svc.language where code = r.language::json->> 'id') as language_id
from (select handle, x.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{description}') as x(type text, "text" text,  language text)
      where api_svc.raid.metadata_schema = 'raido-metadata-schema-v2') as r
on conflict do nothing;

-- METADATA SCHEMA V2 & LEGACY
insert into api_svc.description (raid_name, description_type_id, value)
select r.handle as raid_name,
       case when r.type = 'Primary Description' then 2
            else 1 end as description_type_id,
       r.description as value
from (select handle, x.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{descriptions}') as x(type text, "description" text)
      where api_svc.raid.metadata_schema <> 'raido-metadata-schema-v2') as r
on conflict do nothing;

-- /INSERT DESCRIPTIONS


-- INSERT CONTRIBUTORS

insert into api_svc.contributor (pid, schema_id)
select distinct id,
                (select id from api_svc.contributor_schema where uri = "schemaUri") as schema_id
from (select x.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{contributor}') as x(id text, "schemaUri" text, leader boolean,
                                                                             contact boolean, "role" text,
                                                                             "position" text)
      where api_svc.raid.metadata_schema = 'raido-metadata-schema-v2') r
on conflict do nothing;

insert into api_svc.contributor (pid, schema_id)
select distinct id,
                (select id from api_svc.contributor_schema where uri = "identifierSchemeUri") as schema_id
from (select x.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{contributors}') as x(id text, "identifierSchemeUri" text, "roles" jsonb, "positions" jsonb)
      where api_svc.raid.metadata_schema <> 'raido-metadata-schema-v2') r
on conflict do nothing;



insert into api_svc.raid_contributor (raid_name, contributor_id, leader, contact)
select handle,
       (select id from api_svc.contributor where pid = r.id) as contributor_id,
       leader,
       contact
from (select handle, x.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{contributor}') as x(id text, "schemaUri" text, leader boolean,
                                                                             contact boolean, "role" text,
                                                                             "position" text)
      where api_svc.raid.metadata_schema = 'raido-metadata-schema-v2') as r
on conflict do nothing;

insert into api_svc.raid_contributor (raid_name, contributor_id)

select handle,
       (select id from api_svc.contributor where pid = r.id) as contributor_id
from (select handle,  x.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{contributors}') as x(id text, "identifierSchemeUri" text, "roles" jsonb, "positions" jsonb)
      where api_svc.raid.metadata_schema <> 'raido-metadata-schema-v2') r
on conflict do nothing;

insert into api_svc.raid_contributor_position (raid_contributor_id, contributor_position_id, start_date, end_date)
select (select id
        from api_svc.raid_contributor
        where contributor_id = (select id from api_svc.contributor where pid = r.orcid)
          and raid_name = handle)                                     as raid_contributor_id,
       (select id from api_svc.contributor_position where uri = r.id) as contributor_position_id,
       "startDate"                                                    as start_date,
       "endDate"                                                      as end_date

from (select handle, x.id as orcid, xx.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{contributor}') as x(id text, "schemaUri" text, leader boolean,
                                                                             contact boolean, "role" text,
                                                                             "position" jsonb),
           lateral jsonb_to_recordset(x.position) as xx(id text, "schemaUri" text, "startDate" text, "endDate" text)
      where api_svc.raid.metadata_schema = 'raido-metadata-schema-v2') r
on conflict do nothing;

insert into api_svc.raid_contributor_position (raid_contributor_id, contributor_position_id, start_date, end_date)
select (select id
        from api_svc.raid_contributor
        where contributor_id = (select id from api_svc.contributor where pid = r.orcid)
          and raid_name = handle) as raid_contributor_id,
       (select case
                   when position = 'Co-Investigator' then 1
                   when position = 'Contact Person' then 2
                   when position = 'Leader' then 3
                   when position = 'Other Participant' then 4
                   when position = 'Principal Investigator' then 5
                   end)           as contributor_position_id,
       "startDate"                as start_date,
       "endDate"                  as end_date
from (select handle, x.id as orcid, xx.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{contributors}') as x(id text, "identifierSchemeUri" text, "roles" jsonb, "positions" jsonb),
           lateral jsonb_to_recordset(x.positions) as xx(position text, "positionSchemaUri" text, "startDate" text, "endDate" text)
      where api_svc.raid.metadata_schema <> 'raido-metadata-schema-v2') r
on conflict do nothing;

insert into api_svc.raid_contributor_role (raid_contributor_id, contributor_role_id)
select (select id
        from api_svc.raid_contributor
        where contributor_id = (select id from api_svc.contributor where pid = r.orcid)
          and raid_name = handle)                                     as raid_contributor_id,
       (select id from api_svc.contributor_role where uri = r.id) as contributor_role_id
from (select handle, x.id as orcid, xx.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{contributor}') as x(id text, "schemaUri" text, leader boolean,
                                                                             contact boolean, "role" jsonb,
                                                                             "position" jsonb),
           lateral jsonb_to_recordset(x.role) as xx(id text, "schemaUri" text)
      where api_svc.raid.metadata_schema = 'raido-metadata-schema-v2') r
on conflict do nothing;

insert into api_svc.raid_contributor_role (raid_contributor_id, contributor_role_id)
select (select id
        from api_svc.raid_contributor
        where contributor_id = (select id from api_svc.contributor where pid = r.orcid)
          and raid_name = handle) as raid_contributor_id,
       (select id from api_svc.contributor_role where uri like '%' || role || '%')           as contributor_role_id
from (select handle, x.id as orcid, xx.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{contributors}') as x(id text, "identifierSchemeUri" text, "roles" jsonb, "positions" jsonb),
           lateral jsonb_to_recordset(x.roles) as xx(role text, "roleSchemeUri" text)
      where api_svc.raid.metadata_schema <> 'raido-metadata-schema-v2') r
on conflict do nothing;

-- /INSERT CONTRIBUTORS

-- INSERT ORGANISATIONS

insert into api_svc.organisation (pid, schema_id)
select distinct id,
                (select id from api_svc.organisation_schema where uri = "schemaUri") as schema_id
from (select x.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{organisation}') as x(id text, "schemaUri" text, "role" jsonb)
      where api_svc.raid.metadata_schema = 'raido-metadata-schema-v2') r
on conflict do nothing;

insert into api_svc.organisation (pid, schema_id)
select distinct id, 1 as schema_id
from (select x.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{organisations}') as x(id text, "roleSchemeUri" text, "roles" jsonb)
      where api_svc.raid.metadata_schema <> 'raido-metadata-schema-v2') r
on conflict do nothing;

insert into api_svc.raid_organisation (raid_name, organisation_id)
select handle,
       (select id from api_svc.organisation where pid = r.id) as organisation_id
from (select handle, x.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{organisation}') as x(id text, "schemaUri" text, "role" jsonb)
      where api_svc.raid.metadata_schema = 'raido-metadata-schema-v2') as r
on conflict do nothing;

insert into api_svc.raid_organisation (raid_name, organisation_id)
select handle,
       (select id from api_svc.organisation where pid = r.id) as organisation_id
from (select handle, x.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{organisations}') as x(id text, "roleSchemeUri" text, "roles" jsonb)
      where api_svc.raid.metadata_schema <> 'raido-metadata-schema-v2') r
on conflict do nothing;

insert into api_svc.raid_organisation_role (raid_organisation_id, organisation_role_id, start_date, end_date)
select (select id
        from api_svc.raid_organisation
        where organisation_id = (select id from api_svc.organisation where pid = r.ror) and raid_name = handle) as raid_organisation_id,
       (select id from api_svc.organisation_role where uri = r.id) as organisation_role_id,
       "startDate" as start_date,
       "endDate" as end_date
from (select handle, x.id as ror, xx.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{organisation}') as x(id text, "schemaUri" text, "role" jsonb),
           lateral jsonb_to_recordset(x.role) as xx(id text, "schemaUri" text, "startDate" text, "endDate" text)
      where api_svc.raid.metadata_schema = 'raido-metadata-schema-v2') r
on conflict do nothing;

insert into api_svc.raid_organisation_role (raid_organisation_id, organisation_role_id, start_date, end_date)
select (select id
        from api_svc.raid_organisation
        where organisation_id = (select id from api_svc.organisation where pid = r.ror) and raid_name = handle) as raid_organisation_id,
       (select case
                   when role = 'Contractor' then 1
                   when role = 'Lead Research Organisation' then 2
                   when role = 'Other Organisation' then 3
                   when role = 'Other Research Organisation' then 4
                   when role = 'Partner Organisation' then 5
                   end
       ) as organisation_role_id,
       "startDate" as start_date,
       "endDate" as end_date
from (select handle, x.id as ror, xx.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{organisations}') as x(id text, "identifierSchemeUri" text, "roles" jsonb),
           lateral jsonb_to_recordset(x.roles) as xx(role text, "roleSchemeUri" text, "startDate" text, "endDate" text)
      where api_svc.raid.metadata_schema <> 'raido-metadata-schema-v2') r
on conflict do nothing;

-- /INSERT ORGANISATIONS

-- INSERT RELATED OBJECTS
insert into api_svc.related_object (pid, schema_uri)
select
    id as pid,
    (select id from api_svc.related_object_schema where uri = "schemaUri") as schema_uri
from (select x.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{relatedObject}') as x(id text, "schemaUri" text, type jsonb, category jsonb)
      where api_svc.raid.metadata_schema = 'raido-metadata-schema-v2' and metadata ->> 'relatedObject' is not null) r
on conflict do nothing;

insert into api_svc.related_object (pid, schema_uri)
select
    "relatedObject" as pid,
    (select id from api_svc.related_object_schema where uri = "relatedObjectSchemeUri") as schema_uri
from (select x.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{relatedObjects}') as x("relatedObject" text, "relatedObjectType" text, "relatedObjectCategory" text, "relatedObjectSchemeUri" text, "relatedObjectTypeSchemeUri" text)
      where api_svc.raid.metadata_schema <> 'raido-metadata-schema-v2' and metadata ->> 'relatedObjects' is not null) r
on conflict do nothing;

insert into api_svc.raid_related_object (raid_name, related_object_id, related_object_type_id)
select
    handle as raid_name,
    (select id from api_svc.related_object where pid = related_object_id) as related_object_id,
    (select id from api_svc.related_object_type where uri = related_object_type_id) as related_object_type_id
from (select handle, x.id as related_object_id, xx.id as related_object_type_id
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{relatedObject}') as x(id text, "schemaUri" text, type jsonb, category jsonb),
           lateral jsonb_to_record(type) as xx(id text, "schemaUri" text)
      where api_svc.raid.metadata_schema = 'raido-metadata-schema-v2' and metadata ->> 'relatedObject' is not null) r
on conflict do nothing;

insert into api_svc.raid_related_object (raid_name, related_object_id, related_object_type_id)
select
    handle as raid_name,
    (select id from api_svc.related_object where pid = related_object_id) as related_object_id,
    (select id from api_svc.related_object_type where uri = related_object_type_id) as related_object_type_id
from (select handle, "relatedObject" as related_object_id, "relatedObjectType" as related_object_type_id
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{relatedObjects}') as x("relatedObject" text, "relatedObjectType" text, "relatedObjectCategory" text, "relatedObjectSchemeUri" text, "relatedObjectTypeSchemeUri" text)

      where api_svc.raid.metadata_schema <> 'raido-metadata-schema-v2' and metadata ->> 'relatedObjects' is not null) r
on conflict do nothing;

insert into api_svc.raid_related_object_category (raid_related_object_id, related_object_category_id)
select
    (select rro.id from api_svc.raid_related_object rro join api_svc.related_object ro on rro.related_object_id = ro.id where rro.raid_name = r.handle and ro.pid = r.related_object_id) as raid_related_object_id,
    (select id from api_svc.related_object_category where uri = related_object_category_id) as related_object_category_id
from (select handle, x.id as related_object_id, xx.id as related_object_category_id
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{relatedObject}') as x(id text, "schemaUri" text, type jsonb, category jsonb),
           lateral jsonb_to_record(category) as xx(id text, "schemaUri" text)
      where api_svc.raid.metadata_schema = 'raido-metadata-schema-v2' and metadata ->> 'relatedObject' is not null) r
on conflict do nothing;

insert into api_svc.raid_related_object_category (raid_related_object_id, related_object_category_id)
select
    (select rro.id from api_svc.raid_related_object rro join api_svc.related_object ro on rro.related_object_id = ro.id where rro.raid_name = r.handle and ro.pid = r.related_object_id) as raid_related_object_id,
    (select case
                when related_object_category = 'Input' then 1
                when related_object_category = 'Output' then 3
                else 2
                end) as related_object_category_id
from (select handle, "relatedObject" as related_object_id, "relatedObjectCategory" as related_object_category
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{relatedObjects}') as x("relatedObject" text, "relatedObjectType" text, "relatedObjectCategory" text, "relatedObjectSchemeUri" text, "relatedObjectTypeSchemeUri" text)
      where api_svc.raid.metadata_schema <> 'raido-metadata-schema-v2' and metadata ->> 'relatedObjects' is not null) r
on conflict do nothing;

-- /INSERT RELATED OBJECTS

-- INSERT ALTERNATE IDENTIFIERS
insert into api_svc.raid_alternate_identifier (raid_name, id, type)
select handle, id,     type
from (select handle, x.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{alternateIdentifier}') as x("id" text, "type" text)
      where api_svc.raid.metadata_schema = 'raido-metadata-schema-v2' and metadata ->> 'alternateIdentifier' is not null) r
on conflict do nothing;

insert into api_svc.raid_alternate_identifier (raid_name, id, type)
select handle, "alternateIdentifier", "alternateIdentifierType"
from (select handle, x.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{alternateIdentifiers}') as x("alternateIdentifier" text, "alternateIdentifierType" text)
      where api_svc.raid.metadata_schema <> 'raido-metadata-schema-v2'
        and metadata ->> 'alternateIdentifiers' is not null) r
on conflict do nothing;

-- /INSERT ALTERNATE IDENTIFIERS
-- INSERT ALTERNATE URLS

insert into api_svc.raid_alternate_url (raid_name, url)
select handle, "url"
from (select handle, x.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{alternateUrl}') as x("url" text)
      where api_svc.raid.metadata_schema = 'raido-metadata-schema-v2'
        and metadata ->> 'alternateUrl' is not null) r
on conflict do nothing;

insert into api_svc.raid_alternate_url (raid_name, url)
select handle, "url"
from (select handle, x.*
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{alternateUrls}') as x("url" text)
      where api_svc.raid.metadata_schema <> 'raido-metadata-schema-v2'
        and metadata ->> 'alternateUrls' is not null) r
on conflict do nothing;

-- /INSERT ALTERNATE URLS

-- INSERT RELATED RAIDS
insert into api_svc.related_raid (raid_name, related_raid_name, related_raid_type_id)
select handle, id,
       (select id from api_svc.related_raid_type where uri = related_raid_type_id)
from (select handle, x.id, xx.id as related_raid_type_id
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{relatedRaid}') as x(id text, type jsonb),
           lateral jsonb_to_record(x.type) as xx(id text, type text)
      where api_svc.raid.metadata_schema = 'raido-metadata-schema-v2'
        and metadata ->> 'relatedRaid' is not null) r
on conflict do nothing;

insert into api_svc.related_raid (raid_name, related_raid_name, related_raid_type_id)
select handle, id,
       (select id from api_svc.related_raid_type where uri = r.related_raid_type_id)
from (select handle, x."relatedRaid" as id, "relatedRaidType" as related_raid_type_id
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{relatedRaids}') as x("relatedRaid" text, "relatedRaidType" text, "relatedRaidTypeSchemeUri" text)
      where api_svc.raid.metadata_schema <> 'raido-metadata-schema-v2'
        and metadata ->> 'relatedRaids' is not null) r
on conflict do nothing;

-- /INSERT RELATED RAIDS
-- INSERT SUBJECTS

insert into api_svc.raid_subject (raid_name, subject_type_id)
select handle,
       (select id from api_svc.subject_type where r.id like '%' || id)
from (select handle, x.id
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{subject}') as x(id text, "schemaUri" text, keyword jsonb)
      where api_svc.raid.metadata_schema = 'raido-metadata-schema-v2'
        and metadata ->> 'subject' is not null) r
on conflict do nothing;

insert into api_svc.raid_subject (raid_name, subject_type_id)
select handle,
       (select id from api_svc.subject_type where r.id like '%' || id)
from (select handle, x.subject as id
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{subjects}') as x(subject text, "subjectKeyword" text, "subjectSchemeUri" text)
      where api_svc.raid.metadata_schema <> 'raido-metadata-schema-v2'
        and metadata ->> 'subjects' is not null) r
on conflict do nothing;

insert into api_svc.raid_subject_keyword (raid_subject_id, keyword, language_id)
select (select id
        from api_svc.raid_subject
        where raid_name = r.handle and r.subject_type_id like '%' || subject_type_id) as raid_subject_id,
       r.keyword_text                                                                 as keyword,
       (select id from api_svc.language where code = r.language_code)                 as language_id
from (select handle, x.id as subject_type_id, xx.text as keyword_text, xxx.id as language_code
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{subject}') as x(id text, "schemaUri" text, keyword jsonb),
           lateral jsonb_to_recordset(keyword) as xx("text" text, language jsonb),
           lateral jsonb_to_record(language) as xxx(id text, "schemaUri" text)
      where api_svc.raid.metadata_schema = 'raido-metadata-schema-v2'
        and metadata ->> 'subject' is not null
      group by handle, x.id, xx.text, xxx.id
      having xx.text is not null) r
on conflict do nothing;

insert into api_svc.raid_subject_keyword (raid_subject_id, keyword)
select (select id
        from api_svc.raid_subject
        where raid_name = r.handle and r.subject_type_id like '%' || subject_type_id) as raid_subject_id,
       r.keyword_text                                                                 as keyword
from (select handle, subject as subject_type_id, "subjectKeyword" as keyword_text
      from api_svc.raid,
           jsonb_to_recordset(api_svc.raid.metadata #> '{subjects}') as x(subject text, "subjectKeyword" text, "subjectSchemeUri" text)
      where api_svc.raid.metadata_schema <> 'raido-metadata-schema-v2'
        and metadata ->> 'subjects' is not null) r
on conflict do nothing;

-- /INSERT SUBJECTS

-- INSERT TRADITIONAL KNOWLEDGE LABELS



-- /INSERT TRADITIONAL KNOWLEDGE LABELS







END TRANSACTION;
