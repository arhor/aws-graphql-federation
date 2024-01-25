CREATE TABLE IF NOT EXISTS "outbox_events"
(
    "id"                UUID        NOT NULL PRIMARY KEY,
    "type"              VARCHAR(64) NOT NULL,
    "payload"           JSONB       NOT NULL,
    "headers"           JSONB       NOT NULL,
    "created_date_time" TIMESTAMP   NOT NULL
) WITH (OIDS = FALSE);

CREATE INDEX IDX__outbox_events__created_date_time
    ON "outbox_events" ("created_date_time" ASC);
