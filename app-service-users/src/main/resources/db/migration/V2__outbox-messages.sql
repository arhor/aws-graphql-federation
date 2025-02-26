CREATE TABLE IF NOT EXISTS "outbox_messages" (
    "id"                UUID        NOT NULL PRIMARY KEY,
    "type"              VARCHAR(64) NOT NULL,
    "data"              JSONB       NOT NULL,
    "trace_id"          UUID        NOT NULL,
    "created_date_time" TIMESTAMP   NOT NULL
);

CREATE INDEX IDX__outbox_messages__created_date_time
    ON "outbox_messages" ("created_date_time" ASC);
