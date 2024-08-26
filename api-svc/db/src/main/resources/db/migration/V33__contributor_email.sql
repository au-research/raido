begin transaction;

alter table api_svc.contributor
    add column uuid varchar,
    add column status varchar,
    alter column pid drop not null;

end transaction;