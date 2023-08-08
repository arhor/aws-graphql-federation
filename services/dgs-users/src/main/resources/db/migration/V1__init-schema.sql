CREATE TABLE IF NOT EXISTS "users"
(
    "id"                   BIGSERIAL        NOT NULL PRIMARY KEY,
    "username"             VARCHAR(128)     NOT NULL UNIQUE,
    "password"             VARCHAR(1024)    NULL,
    "version"              BIGINT           NOT NULL,
    "created_date_time"    TIMESTAMP        NOT NULL,
    "updated_date_time"    TIMESTAMP        NULL
) WITH (OIDS = FALSE);
