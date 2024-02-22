CREATE TABLE tenant (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   name VARCHAR(255),
   cpf VARCHAR(11),
   phone VARCHAR(11),
   rg VARCHAR(9),
   birth_date date,
   birth_local VARCHAR(255),
   is_active BOOLEAN,
   contract_id UUID,
   CONSTRAINT pk_tenant PRIMARY KEY (id)
);

ALTER TABLE tenant ADD CONSTRAINT uc_tenant_cpf UNIQUE (cpf);

ALTER TABLE tenant ADD CONSTRAINT uc_tenant_rg UNIQUE (rg);

ALTER TABLE tenant ADD CONSTRAINT FK_TENANT_ON_CONTRACT FOREIGN KEY (contract_id) REFERENCES contract (uuid);