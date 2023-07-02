
alter table users add column  email VARCHAR(45) NOT NULL;
alter table users add constraint  email_unique UNIQUE (email);
CREATE TABLE roles (
  role_id SERIAL PRIMARY KEY,
  role_name VARCHAR(45) NOT NULL
);

CREATE TABLE roles_users (
  user_id INT NOT NULL,
  role_id INT NOT NULL,
  CONSTRAINT user_fk FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
  CONSTRAINT role_fk FOREIGN KEY (role_id) REFERENCES roles (role_id)
);
INSERT INTO roles (role_name) VALUES ('USER');
INSERT INTO roles (role_name) VALUES ('ADMIN');
