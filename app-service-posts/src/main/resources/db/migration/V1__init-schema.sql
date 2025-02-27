CREATE TABLE IF NOT EXISTS "user_representations" (
    "id"       UUID NOT NULL PRIMARY KEY,
    "features" INT  NOT NULL
);

CREATE TABLE IF NOT EXISTS "posts" (
    "id"                UUID         NOT NULL PRIMARY KEY,
    "user_id"           UUID         NULL,
    "title"             VARCHAR(512) NOT NULL,
    "content"           TEXT         NOT NULL,
    "version"           BIGINT       NOT NULL,
    "created_date_time" TIMESTAMP    NOT NULL,
    "updated_date_time" TIMESTAMP    NULL,

    CONSTRAINT "FK__posts__user_id"
        FOREIGN KEY ("user_id") REFERENCES "user_representations" ("id")
            ON UPDATE CASCADE
            ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS "tags" (
    "id"   UUID        NOT NULL PRIMARY KEY,
    "name" VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS "posts_have_tags" (
    "post_id" UUID NOT NULL,
    "tag_id"  UUID NOT NULL,

    CONSTRAINT "PK__posts_have_tags__post_id__tag_id"
        PRIMARY KEY ("post_id", "tag_id"),

    CONSTRAINT "FK__posts_have_tags__post_id"
        FOREIGN KEY ("post_id") REFERENCES "posts" ("id")
            ON UPDATE CASCADE
            ON DELETE CASCADE,

    CONSTRAINT "FK__posts_have_tags__tag_id"
        FOREIGN KEY ("tag_id") REFERENCES "tags" ("id")
            ON UPDATE CASCADE
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "posts_have_user_likes" (
    "post_id"           UUID      NOT NULL,
    "user_id"           UUID      NOT NULL,
    "created_date_time" TIMESTAMP NOT NULL,

    CONSTRAINT "PK__posts_have_user_likes__post_id__user_id"
        PRIMARY KEY ("post_id", "user_id"),

    CONSTRAINT "FK__posts_have_user_likes__post_id"
        FOREIGN KEY ("post_id") REFERENCES "posts" ("id")
            ON UPDATE CASCADE
            ON DELETE CASCADE,

    CONSTRAINT "FK__posts_have_user_likes__user_id"
        FOREIGN KEY ("user_id") REFERENCES "user_representations" ("id")
            ON UPDATE CASCADE
            ON DELETE CASCADE
);
