CREATE TABLE IF NOT EXISTS "comments"
(
    "id"                   BIGSERIAL        NOT NULL PRIMARY KEY,
    "user_id"              BIGINT           NULL,
    "post_id"              BIGINT           NOT NULL,
    "content"              VARCHAR(1024)    NOT NULL,
    "version"              BIGINT           NOT NULL,
    "created_date_time"    TIMESTAMP        NOT NULL,
    "updated_date_time"    TIMESTAMP        NULL
) WITH (OIDS = FALSE);
