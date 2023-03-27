-- CREATE TABLE :"STASH_SCHEMA_NAME".app_user AS SELECT * FROM :"API_SCHEMA_NAME".app_user;

-- insert into :"API_SCHEMA_NAME".app_user
-- select *
-- from :"STASH_SCHEMA_NAME".app_user
-- ;

insert into api_svc.app_user (
  service_point_id, email, client_id, subject,
  id_provider, role, enabled, token_cutoff,
  date_created)
select
  service_point_id, email, client_id, subject,
  id_provider::api_svc.id_provider, 
  role::api_svc.user_role, 
  enabled, token_cutoff, date_created
from :"STASH_SCHEMA_NAME".app_user
ON CONFLICT (email, client_id, subject) where enabled=true DO NOTHING
;
