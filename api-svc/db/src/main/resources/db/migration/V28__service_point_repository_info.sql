begin transaction;

alter table api_svc.service_point
    add column repository_id varchar,
    add column prefix        varchar,
    add column password      varchar;

end transaction;