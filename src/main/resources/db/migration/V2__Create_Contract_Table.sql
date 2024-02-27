CREATE TABLE contract (
  uuid UUID NOT NULL,
   start_date date,
   end_date date,
   price DECIMAL,
   description VARCHAR(255),
   due_date date,
   CONSTRAINT pk_contract PRIMARY KEY (uuid)
);

CREATE TABLE contract_apartment (
  apartment_id BIGINT,
   contract_id UUID NOT NULL,
   CONSTRAINT pk_contract_apartment PRIMARY KEY (contract_id)
);

ALTER TABLE contract_apartment ADD CONSTRAINT uc_contract_apartment_contract UNIQUE (contract_id);

ALTER TABLE contract_apartment ADD CONSTRAINT fk_conapa_on_apartment FOREIGN KEY (apartment_id) REFERENCES apartment (id);

ALTER TABLE contract_apartment ADD CONSTRAINT fk_conapa_on_contract FOREIGN KEY (contract_id) REFERENCES contract (uuid);