-- V2 because the empty baseline is V1  
create table raid (
    handle text constraint raid_pkey primary key not null,
    owner text not null,
    content_path text not null,
    content_index text not null,
    name text not null,
    description text not null,
    start_date timestamp without time zone not null,
    creation_date timestamp without time zone not null,
    s3_export jsonb not null
);

comment on table raid is
'imported from RAiD-RAiDLiveDB-1SX7NYTSOSUKX-RAiDTable-1PO1W2ASWY0OV';

