CREATE TABLE IF NOT EXISTS "articles"
(
    "id"                   BIGSERIAL       NOT NULL PRIMARY KEY,
    "user_id"              BIGINT          NULL,
    "header"               VARCHAR(512)    NOT NULL,
    "banner"               VARCHAR(512)    NULL,
    "content"              TEXT            NOT NULL,
    "version"              BIGINT          NOT NULL,
    "created_date_time"    TIMESTAMP       NOT NULL,
    "updated_date_time"    TIMESTAMP       NULL
) WITH (OIDS = FALSE);

CREATE TABLE IF NOT EXISTS "tags"
(
    "id"                   BIGSERIAL      NOT NULL PRIMARY KEY,
    "name"                 VARCHAR(50)    NOT NULL UNIQUE
) WITH (OIDS = FALSE);

CREATE TABLE IF NOT EXISTS "articles_has_tags"
(
    "id"                   BIGSERIAL      NOT NULL PRIMARY KEY,
    "article_id"           BIGINT         NOT NULL,
    "tag_id"               BIGINT         NOT NULL,

    CONSTRAINT "UQ__articles_has_tags__article_id__tag_id"
        UNIQUE ("article_id", "tag_id"),

    CONSTRAINT "FK__articles_has_tags__articles"
        FOREIGN KEY ("article_id")
            REFERENCES "articles" ("id")
            ON UPDATE CASCADE
            ON DELETE CASCADE,

    CONSTRAINT "FK__articles_has_tags__tags"
        FOREIGN KEY ("tag_id")
            REFERENCES "tags" ("id")
            ON UPDATE CASCADE
            ON DELETE CASCADE
) WITH (OIDS = FALSE);
