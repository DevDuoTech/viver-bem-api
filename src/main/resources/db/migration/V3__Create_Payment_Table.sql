CREATE TABLE payment
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    value          DOUBLE PRECISION,
    payment_date   date,
    payment_type   VARCHAR(255),
    payment_status VARCHAR(255),
    competency     date,
    tenant_id      BIGINT NOT NULL,
    CONSTRAINT pk_payment PRIMARY KEY (id)
);

ALTER TABLE payment
    ADD CONSTRAINT FK_PAYMENT_ON_TENANT FOREIGN KEY (tenant_id) REFERENCES tenant (id);