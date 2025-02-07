CREATE TABLE tenant
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255),
    email       VARCHAR(255) UNIQUE,
    cpf         VARCHAR(11) UNIQUE,
    phone       VARCHAR(11),
    rg          VARCHAR(9) UNIQUE,
    birth_date  TIMESTAMP WITHOUT TIME ZONE,
    birth_local VARCHAR(255),
    is_active   BOOLEAN
);

ALTER TABLE tenant
    ADD CONSTRAINT uc_tenant_cpf UNIQUE (cpf);

ALTER TABLE tenant
    ADD CONSTRAINT uc_tenant_email UNIQUE (email);

ALTER TABLE tenant
    ADD CONSTRAINT uc_tenant_rg UNIQUE (rg);