create table import_history (
  action_date timestamp without time zone,
  status text not null,
  error text null,
  details jsonb not null
);

comment on table import_history is
'history of data import runs';


