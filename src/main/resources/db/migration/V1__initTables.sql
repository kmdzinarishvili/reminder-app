
CREATE TABLE reminders (
	reminder_id serial PRIMARY KEY,
	title VARCHAR ( 255 ) NOT NULL,
	recurrence VARCHAR ( 50 ) NOT NULL,
    reminder_date date NOT NULL,
    reminder_time time(4) DEFAULT '00:00',
    attachment bytea
);


CREATE TABLE labels (
    label_id serial PRIMARY KEY,
    label_name varchar( 50 ) NOT NULL
);

CREATE TABLE labels_reminders(
    reminder_id INTEGER,
    label_id INTEGER,
    PRIMARY KEY(reminder_id, label_id)
);

CREATE TABLE users(
    user_id serial PRIMARY KEY,
    username VARCHAR ( 50 ) NOT NULL,
    password varchar (255) NOT NULL,
    role varchar(50)
);


CREATE TABLE reminders_users(
    reminder_id INTEGER,
    user_id INTEGER,
    PRIMARY KEY(reminder_id, user_id)
);


CREATE TABLE modifications (
    reminder_id serial PRIMARY KEY,
	title VARCHAR ( 255 ) NOT NULL,
	recurrence VARCHAR ( 50 ) NOT NULL,
    reminder_date date NOT NULL,
    reminder_time  time(4) DEFAULT '00:00',
    modification_date timestamp default CURRENT_TIMESTAMP(4),
    attachment bytea
);