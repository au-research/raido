begin transaction;

alter table api_svc.contributor
    add column email varchar,
    add column token varchar;

alter table api_svc.raid_contributor
    add column status varchar;

end transaction;