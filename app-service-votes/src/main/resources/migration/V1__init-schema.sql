CREATE TABLE "votes" (
    "id"                UUID      NOT NULL PRIMARY KEY,
    "user_id"           UUID      NOT NULL,
    "entity_id"         UUID      NOT NULL,
    "entity_type"       TEXT      NOT NULL CHECK ("entity_type" IN ('post', 'comment')),
    "value"             SMALLINT  NOT NULL CHECK ("value" IN (-1, 1)),
    "created_date_time" TIMESTAMP NOT NULL,
    "updated_date_time" TIMESTAMP NULL,
    UNIQUE ("user_id", "entity_id", "entity_type")
) PARTITION BY LIST ("entity_type");

CREATE TABLE "posts_votes" PARTITION OF "votes"
    FOR VALUES IN ('post');

CREATE TABLE "comments_votes" PARTITION OF "votes"
    FOR VALUES IN ('comment');

CREATE INDEX "idx_votes_user"
    ON "votes" ("user_id");

CREATE INDEX "idx_votes_entity"
    ON "votes" ("entity_id", "entity_type");
