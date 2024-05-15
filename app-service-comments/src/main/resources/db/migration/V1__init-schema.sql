CREATE TABLE IF NOT EXISTS "user_representations"
(
    "id" UUID NOT NULL PRIMARY KEY
) WITH (OIDS = FALSE);

CREATE TABLE IF NOT EXISTS "post_representations"
(
    "id"                UUID    NOT NULL PRIMARY KEY,
    "comments_disabled" BOOLEAN NOT NULL
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

    CONSTRAINT "FK__comments__user_representations"
        FOREIGN KEY ("user_id")
            REFERENCES "user_representations" ("id")
            ON UPDATE CASCADE
            ON DELETE SET NULL,

    CONSTRAINT "FK__comments__post_representations"
        FOREIGN KEY ("post_id")
            REFERENCES "post_representations" ("id")
            ON UPDATE CASCADE
            ON DELETE CASCADE
) WITH (OIDS = FALSE);
