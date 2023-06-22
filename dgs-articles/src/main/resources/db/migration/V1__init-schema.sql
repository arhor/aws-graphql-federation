CREATE TABLE IF NOT EXISTS "articles"
(
    "id"                   BIGSERIAL       NOT NULL PRIMARY KEY,
    "user_id"              VARCHAR(512)    NULL
    "header"               VARCHAR(512)    NOT NULL,
    "banner"               VARCHAR(512)    NULL,
    "content"              TEXT            NOT NULL,
    "version"              BIGINT          NOT NULL,
    "created_date_time"    TIMESTAMP       NOT NULL,
    "updated_date_time"    TIMESTAMP       NULL
) WITH (OIDS = FALSE);
