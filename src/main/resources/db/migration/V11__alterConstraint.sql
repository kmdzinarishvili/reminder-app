alter table reminders drop constraint fk_user_id;
alter table reminders add constraint fk_user_id foreign key (user_id)
references users(user_id) on delete cascade;