-- expected default schema raid_v1_import 

select * from import_history
;

select 'raids', count(*) from raid
union
select 'associations', count(*) from association_index
;

select owner, count(*)
from raid
group by owner
;

select owner, count(*)
from raid
where creation_date > '2019-01-01'
group by owner
;


select owner, *
from raid
where s3_export ->'Item' ->'meta' is null
;

select owner, *
from raid
where s3_export ->'Item' ->'meta'->'M'->'description' is null
;

select owner, *
from raid
where s3_export ->'Item' ->'meta'->'M'->'name' is null
;

select distinct field
from (
  select jsonb_object_keys(s3_export->'Item') as field
  from raid
) as subquery
;

select
  distinct field
from (
  select jsonb_object_keys(s3_export->'Item'->'meta') as field
  from raid
) as subquery
;

select * 
from raid
where name ilike 'sto%'
;

show timezone
;

set timezone to 'utc';
set timezone to 'australia/sydney';
select
  current_setting('timezone') db_tz,
  s3_export->'Item'->'creationDate'->'S' json_timestamp,
  creation_date raw_db_ts, -- timestamp without timezone
  creation_date at time zone 'UTC' at time zone 'australia/sydney' as at_at_syd,
  creation_date at time zone 'australia/sydney' as at_tz_syd,
  creation_date::timestamptz cast_tstz,
  timezone('australia/sydney', creation_date) as tz_function_syd
from raid
where handle = '102.100.100/432142'
;

select *
from raid
-- between is inclusive, but when just specificying a date, 
-- you get the first second of that day
where creation_date between '2022-06-01' and '2022-07-01'
order by creation_date desc
;

select
  date_trunc('month',creation_date)
    as production_to_month,
  count(*) as count
from raid
group by date_trunc('month',creation_date)
order by date_trunc('month',creation_date) desc
;

select date_trunc('month',creation_date), owner, count(*)
from raid
group by date_trunc('month',creation_date), owner
order by date_trunc('month',creation_date) desc
;

select date_trunc('year',creation_date), owner, count(*)
from raid
group by date_trunc('year',creation_date), owner
order by date_trunc('year',creation_date) desc
;

select count(*)
from association_index
;

select *
from association_index ait
where ait.handle not in
  (select rt.handle
  from raid rt)
;

select * 
from raid rt 
where rt.handle not in 
(
  select ait.handle
  from association_index ait
  )
;

select * 
from raid
where handle like '%993%'
;

select * from association_index
where role = ''
;

select count(*) from association_index
where role = ''
;

select role, count(*)
from association_index
group by role
;

select type, count(*)
from association_index
group by type
;

select type, role, count(*)
from association_index
group by type, role 
;

select * 
from association_index
where type = 'service' and role  = ''
;

select * 
from association_index
where type = 'institution'
order by start_date
;

select * from association_index
where handle in (
'102.100.100/399903',
'102.100.100/441933'
)
;

select * 
from metadata
;

select * 
from metadata md
where md.name not in
  (
  select rt.owner
  from raid rt
  )
;
  
select * 
from token
;

select r.owner, ai.owner_name, r.*, ai.*
from raid r join association_index ai on r.handle = ai.handle
where 
--   r.handle = '102.100.100/391166'
 r.owner != ai.owner_name
;

select count(*)
from raid r join association_index ai on r.handle = ai.handle
where r.owner = 'RDM@UQ'
;