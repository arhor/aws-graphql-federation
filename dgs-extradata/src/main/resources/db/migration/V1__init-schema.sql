CREATE TABLE IF NOT EXISTS "extra_data"
(
    "id"             VARCHAR(36)    NOT NULL PRIMARY KEY,
    "entity_id"      VARCHAR(36)    NOT NULL,
    "entity_type"    VARCHAR(20)    NOT NULL,
    "data"           JSONB          NOT NULL
) WITH (OIDS = FALSE);

CREATE INDEX IF NOT EXISTS "entity_id_idx"
    ON "extra_data" USING HASH ("entity_id");

CREATE INDEX IF NOT EXISTS "entity_type_idx"
    ON "extra_data" USING HASH ("entity_type");
