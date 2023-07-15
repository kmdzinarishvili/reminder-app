alter table users add column registration_date date not null default CURRENT_DATE;
alter table users add column activity_date date;
alter table users add column timezone_offset_hours float(1);
alter table users add column days_before_reminder_delete int;
alter table users drop column role;


