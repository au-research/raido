alter table service_point add column identifier_owner char(25) not null;

update service_point set identifier_owner = 'https://ror.org/038sjwq14' where id in (20000000,20000001);
update service_point set identifier_owner = 'https://ror.org/00rqy9422' where id = 20000003;
update service_point set identifier_owner = 'https://ror.org/02stey378' where id in 20000004;
