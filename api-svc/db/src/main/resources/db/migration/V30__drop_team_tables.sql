begin transaction;

drop table if exists api_svc.team_user;
drop table if exists api_svc.team;

alter table api_svc.service_point
    add column group_id char(36);

end transaction;