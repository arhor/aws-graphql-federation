---------- External entities representation tables ----------

CREATE TABLE IF NOT EXISTS "user_representations" (
    "id" UUID NOT NULL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS "post_representations" (
    "id"      UUID NOT NULL PRIMARY KEY,
    "user_id" UUID NULL,

    CONSTRAINT "FK__post_representations__user_id"
        FOREIGN KEY ("user_id") REFERENCES "user_representations" ("id")
            ON UPDATE CASCADE
            ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS "comment_representations" (
    "id"      UUID NOT NULL PRIMARY KEY,
    "user_id" UUID NULL,
    "post_id" UUID NULL,

    CONSTRAINT "FK__comment_representations__user_id"
        FOREIGN KEY ("user_id") REFERENCES "user_representations" ("id")
            ON UPDATE CASCADE
            ON DELETE SET NULL,

    CONSTRAINT "FK__comment_representations__post_id"
        FOREIGN KEY ("post_id") REFERENCES "post_representations" ("id")
            ON UPDATE CASCADE
            ON DELETE CASCADE
);

---------- Internal entity tables ----------

CREATE TABLE IF NOT EXISTS "votes" (
    "id"                UUID      NOT NULL PRIMARY KEY,
    "user_id"           UUID      NOT NULL,
    "entity_id"         UUID      NOT NULL,
    "entity_type"       TEXT      NOT NULL,
    "value"             SMALLINT  NOT NULL,
    "version"           BIGINT    NOT NULL,
    "created_date_time" TIMESTAMP NOT NULL,
    "updated_date_time" TIMESTAMP NULL,

    CONSTRAINT "UQ__votes__user_id__entity_id__entity_type"
        UNIQUE ("user_id", "entity_id", "entity_type"),

    CONSTRAINT "CH__votes__entity_type"
        CHECK ("entity_type" IN ('POST', 'COMMENT')),

    CONSTRAINT "CH__votes__value"
        CHECK ("value" IN (-1, 1))
) PARTITION BY LIST ("entity_type");

---------- Partitions ----------

CREATE TABLE IF NOT EXISTS "posts_votes"
    PARTITION OF "votes"
    FOR VALUES IN ('POST')
    CONSTRAINT "FK__posts_votes__user_id"
        FOREIGN KEY ("user_id") REFERENCES "user_representations" ("id")
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT "FK__posts_votes__entity_id"
        FOREIGN KEY ("entity_id") REFERENCES "post_representations" ("id")
            ON DELETE CASCADE
            ON UPDATE CASCADE;

CREATE TABLE IF NOT EXISTS "comments_votes"
    PARTITION OF "votes"
    FOR VALUES IN ('COMMENT')
    CONSTRAINT "FK__comments_votes__user_id"
        FOREIGN KEY ("user_id") REFERENCES "user_representations" ("id")
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT "FK__comments_votes__entity_id"
        FOREIGN KEY ("entity_id") REFERENCES "comment_representations" ("id")
            ON DELETE CASCADE
            ON UPDATE CASCADE;

---------- Indexes ----------

CREATE INDEX "IDX__votes__user_id"
    ON "votes" ("user_id");

CREATE INDEX "IDX__votes__entity_id__entity_type"
    ON "votes" ("entity_id", "entity_type");
