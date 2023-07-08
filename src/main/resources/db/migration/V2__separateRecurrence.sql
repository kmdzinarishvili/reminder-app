create table recurrence_types (
    recurrence_id serial PRIMARY KEY,
    recurrence_name VARCHAR ( 50 ) NOT NULL
);
alter table reminders drop column recurrence;
alter table reminders add column recurrence_id int default 1;
alter table reminders add constraint fk_reminder_recurrence FOREIGN KEY(recurrence_id)
        REFERENCES recurrence_types(recurrence_id);

insert into recurrence_types(recurrence_name)
    values ('NEVER'), ('DAILY'),('WEEKLY'), ('MONTHLY');

alter table reminders drop column reminder_date;
alter table reminders drop column reminder_time;
alter table reminders add column reminder_datetime timestamp;

alter table users add column email varchar(50);
alter table users add constraint unique_email unique(email);
