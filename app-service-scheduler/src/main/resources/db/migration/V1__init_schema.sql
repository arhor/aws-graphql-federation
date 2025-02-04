CREATE TABLE IF NOT EXISTS "scheduled_events"
(
    "id"                UUID          NOT NULL PRIMARY KEY,
    "type"              VARCHAR(64)   NOT NULL ,
    "data"              VARCHAR(1024) NOT NULL,
    "publish_date_time" TIMESTAMP     NOT NULL,
    "created_date_time" TIMESTAMP     NOT NULL
) WITH (OIDS = FALSE);
