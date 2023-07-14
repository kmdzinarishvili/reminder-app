alter table reminders_users add constraint fk_user_id foreign key (user_id) references users(user_id) on delete cascade;
alter table reminders_users add constraint fk_reminder_id foreign key (reminder_id) references reminders(reminder_id) on delete cascade;
alter table reminders_users add column acceptance_status varchar(70);