drop table if exists raid_history;
create table raid_history (
    handle varchar(255) not null,
    revision int not null,
    change_type varchar(8) not null,
    diff text not null,
    created timestamp not null,
    primary key (handle, revision)
);