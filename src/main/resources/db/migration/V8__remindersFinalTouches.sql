alter table reminders add column creation_date timestamp;
ALTER TABLE reminders ALTER COLUMN creation_date SET DEFAULT now();

alter table reminders add column priority int;
alter table reminders add column category_id int;
create table categories(
    category_id serial PRIMARY KEY,
    category_name varchar(70)
);
insert into categories(category_name)
values('WORK'), ('PERSONAL'), ('EDUCATION'), ('ASSIGNED');
alter table reminders add constraint fk_categories foreign key
(category_id) references categories (category_id);

DROP TABLE reminders_users;
alter table reminders add column user_id int;
alter table reminders add constraint fk_user_id foreign key (user_id)
references users(user_id);
alter table reminders add column acceptance_status bool;

alter table reminders alter column reminder_datetime
set data type timestamp without time zone;
