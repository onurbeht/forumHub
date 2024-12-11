alter table if exists usuarios add role varchar(20);
update usuarios set role = 'USER';
alter table if exists usuarios alter column role set not null;
