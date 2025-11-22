CREATE TABLE contract
(
    uuid          UUID NOT NULL,
    start_date    date,
    end_date      date,
    price         DOUBLE PRECISION,
    description   VARCHAR(255),
    due_date      INTEGER,
    has_guarantee BOOLEAN,
    is_active     BOOLEAN,
    tenant_id     BIGINT,
    number_ap     BIGINT,
    created_at    TIMESTAMPTZ NOT NULL,
    updated_at    TIMESTAMPTZ,
    CONSTRAINT pk_contract PRIMARY KEY (uuid)
);

ALTER TABLE contract
    ADD CONSTRAINT uc_contract_number_ap UNIQUE (number_ap);

ALTER TABLE contract
    ADD CONSTRAINT uc_contract_tenant UNIQUE (tenant_id);

ALTER TABLE contract
    ADD CONSTRAINT FK_CONTRACT_ON_NUMBER_AP FOREIGN KEY (number_ap) REFERENCES apartment (number_ap);

ALTER TABLE contract
    ADD CONSTRAINT FK_CONTRACT_ON_TENANT FOREIGN KEY (tenant_id) REFERENCES tenant (id);