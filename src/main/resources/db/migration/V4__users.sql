
alter table users add constraint  email_unique UNIQUE (email);
CREATE TABLE roles (
  role_id SERIAL PRIMARY KEY,
  role_name VARCHAR(45) NOT NULL
);
INSERT INTO roles (role_name) VALUES ('USER');
INSERT INTO roles (role_name) VALUES ('ADMIN');

alter table users add column role_id int;
alter table users add constraint fk_role_id foreign key (role_id) references roles(role_id);


