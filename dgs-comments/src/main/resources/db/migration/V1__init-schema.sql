CREATE TABLE IF NOT EXISTS "users"
(
    "id"                   BIGINT           NOT NULL PRIMARY KEY
) WITH (OIDS = FALSE);

CREATE TABLE IF NOT EXISTS "posts"
(
    "id"                   BIGINT           NOT NULL PRIMARY KEY
) WITH (OIDS = FALSE);

CREATE TABLE IF NOT EXISTS "comments"
(
    "id"                   BIGSERIAL        NOT NULL PRIMARY KEY,
    "user_id"              BIGINT           NULL,
    "post_id"              BIGINT           NOT NULL,
    "content"              VARCHAR(1024)    NOT NULL,
    "version"              BIGINT           NOT NULL,
    "created_date_time"    TIMESTAMP        NOT NULL,
    "updated_date_time"    TIMESTAMP        NULL,

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
