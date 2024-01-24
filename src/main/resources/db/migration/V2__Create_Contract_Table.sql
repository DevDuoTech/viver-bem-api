CREATE TABLE contract (
  id UUID DEFAULT gen_random_uuid() NOT NULL,
  start_date DATE,
  end_date DATE,
  price DECIMAL,
  description VARCHAR(255),
  CONSTRAINT pk_contract PRIMARY KEY (id)
);

CREATE TABLE contract_apartment (
  apartment_id BIGINT,
  contract_id UUID NOT NULL,
  CONSTRAINT pk_contract_apartment PRIMARY KEY (contract_id),
  FOREIGN KEY (contract_id) REFERENCES contract (id)
);