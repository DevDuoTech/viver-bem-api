CREATE TABLE contract (
  uuid UUID DEFAULT gen_random_uuid() NOT NULL,
  start_date DATE,
  end_date DATE,
  price DECIMAL,
  description VARCHAR(255),
  CONSTRAINT pk_contract PRIMARY KEY (uuid)
);

CREATE TABLE contract_apartment (
  apartment_id BIGINT,
  contract_uuid UUID NOT NULL,
  CONSTRAINT pk_contract_apartment PRIMARY KEY (contract_uuid),
  FOREIGN KEY (contract_uuid) REFERENCES contract (uuid)
);