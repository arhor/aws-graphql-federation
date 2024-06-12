CREATE TABLE IF NOT EXISTS "user_representations"
(
    "id"       UUID NOT NULL PRIMARY KEY,
    "features" INT  NOT NULL
) WITH (OIDS = FALSE);

CREATE TABLE IF NOT EXISTS "post_representations"
(
    "id"       UUID NOT NULL PRIMARY KEY,
    "features" INT  NOT NULL
) WITH (OIDS = FALSE);

CREATE TABLE IF NOT EXISTS "comments"
(
    "id"                UUID          NOT NULL PRIMARY KEY,
    "user_id"           UUID          NULL,
    "post_id"           UUID          NOT NULL,
    "prnt_id"           UUID          NULL,
    "content"           VARCHAR(1024) NOT NULL,
    "version"           BIGINT        NOT NULL,
    "created_date_time" TIMESTAMP     NOT NULL,
    "updated_date_time" TIMESTAMP     NULL,

    CONSTRAINT "FK__comments__prnt_id"
        FOREIGN KEY ("prnt_id")
            REFERENCES "comments" ("id")
            ON UPDATE CASCADE
            ON DELETE SET NULL,

    CONSTRAINT "FK__comments__user_id"
        FOREIGN KEY ("user_id")
            REFERENCES "user_representations" ("id")
            ON UPDATE CASCADE
            ON DELETE SET NULL,

    CONSTRAINT "FK__comments__post_id"
        FOREIGN KEY ("post_id")
            REFERENCES "post_representations" ("id")
            ON UPDATE CASCADE
            ON DELETE CASCADE
) WITH (OIDS = FALSE);
