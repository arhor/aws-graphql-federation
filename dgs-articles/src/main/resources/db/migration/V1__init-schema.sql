CREATE TABLE IF NOT EXISTS "articles"
(
    "id"         BIGSERIAL       NOT NULL PRIMARY KEY,
    "user_id"    BIGINT          NOT NULL
    "name"       VARCHAR(512)    NOT NULL,
    "content"    TEXT            NOT NULL
) WITH (OIDS = FALSE);
