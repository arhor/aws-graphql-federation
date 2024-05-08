CREATE TABLE IF NOT EXISTS "users"
(
    "id" UUID NOT NULL PRIMARY KEY
) WITH (OIDS = FALSE);

CREATE TABLE IF NOT EXISTS "posts"
(
    "id" UUID NOT NULL PRIMARY KEY
) WITH (OIDS = FALSE);

CREATE TABLE IF NOT EXISTS "comments"
(
    "id"                UUID          NOT NULL PRIMARY KEY,
    "user_id"           UUID          NULL,
    "post_id"           UUID          NOT NULL,
    "content"           VARCHAR(1024) NOT NULL,
    "version"           BIGINT        NOT NULL,
    "created_date_time" TIMESTAMP     NOT NULL,
    "updated_date_time" TIMESTAMP     NULL,

    CONSTRAINT "FK__comments__users"
        FOREIGN KEY ("user_id")
            REFERENCES "users" ("id")
            ON UPDATE CASCADE
            ON DELETE SET NULL,

    CONSTRAINT "FK__comments__posts"
        FOREIGN KEY ("post_id")
            REFERENCES "posts" ("id")
            ON UPDATE CASCADE
            ON DELETE CASCADE
) WITH (OIDS = FALSE);
