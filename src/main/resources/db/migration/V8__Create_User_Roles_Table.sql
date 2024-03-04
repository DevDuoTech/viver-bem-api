CREATE TABLE user_roles (
  role_id BIGINT NOT NULL,
   user_id BIGINT NOT NULL,
   CONSTRAINT pk_user_roles PRIMARY KEY (role_id, user_id)
);

ALTER TABLE user_roles ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (role_id) REFERENCES role (id);

ALTER TABLE user_roles ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES tb_user (id);