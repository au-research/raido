alter table raid alter column handle type varchar(128);


comment on column raid.handle is
  'Holds the handle (i.e. just prefix/suffix) not the URL.  Usually quite '
  ' short in production, but the max length is set to accommodate int and load'
  ' testing.';
