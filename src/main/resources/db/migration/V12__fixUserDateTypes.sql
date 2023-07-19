alter table users drop column registration_date;
alter table users add column registration_date timestamp without time zone not null default CURRENT_TIMESTAMP(0);
alter table users drop column activity_date;
alter table users add column activity_date timestamp without time zone;