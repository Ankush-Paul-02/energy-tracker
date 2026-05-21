CREATE TABLE devices
(
    id       BIGSERIAL PRIMARY KEY,

    name     VARCHAR(255),
    type     VARCHAR(50),
    location VARCHAR(255),

    user_id  BIGINT,

    CONSTRAINT fk_devices_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);