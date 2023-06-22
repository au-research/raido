update raido.api_svc.service_point set app_writes_enabled = true;

alter table raido.api_svc.service_point alter column app_writes_enabled set not null;
