CREATE TABLE users
(
    id                        BIGSERIAL PRIMARY KEY,

    username                  VARCHAR(100) UNIQUE,
    email                     VARCHAR(150) UNIQUE,

    address                   VARCHAR(255),

    is_alerting               BOOLEAN          NOT NULL DEFAULT FALSE,
    energy_alerting_threshold DOUBLE PRECISION NOT NULL DEFAULT 0
);