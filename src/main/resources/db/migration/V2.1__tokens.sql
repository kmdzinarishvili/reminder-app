CREATE TABLE tokens(
    token_id serial PRIMARY KEY,
    token_value VARCHAR ( 1000 ) NOT NULL,
    revoked boolean,
    expired boolean,
    user_id int,
    constraint fk_user_id foreign key (user_id) references users (user_id)
);
