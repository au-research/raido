alter table token drop constraint token_pkey;
alter table token add primary key (name, environment, date_created);


