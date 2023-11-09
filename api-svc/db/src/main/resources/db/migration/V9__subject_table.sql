DROP TABLE IF EXISTS raido.api_svc.subject;
CREATE TABLE IF NOT EXISTS raido.api_svc.subject (
    id varchar(6) not null,
    name text not null,
    description text,
    note text,
    primary key (id)
);