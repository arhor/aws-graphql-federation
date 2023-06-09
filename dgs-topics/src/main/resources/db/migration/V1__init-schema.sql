CREATE TABLE IF NOT EXISTS "topics"
(
    "id"                   BIGSERIAL        NOT NULL PRIMARY KEY,
    "name"                 VARCHAR(512)     NOT NULL UNIQUE,
    "user_id"              BIGINT           NULL
) WITH (OIDS = FALSE);

CREATE TABLE IF NOT EXISTS "posts"
(
    "id"                   BIGSERIAL        NOT NULL PRIMARY KEY,
    "user_id"              BIGINT           NULL,
    "topic_id"             BIGINT           NOT NULL,
    "content"              VARCHAR(1024)    NOT NULL,
    "version"              BIGINT           NOT NULL,
    "created_date_time"    TIMESTAMP        NOT NULL,
    "updated_date_time"    TIMESTAMP        NULL,

    CONSTRAINT FK_posts_topics_topic_id FOREIGN KEY ("topic_id")
        REFERENCES "topics" ("id")
        ON UPDATE CASCADE
        ON DELETE CASCADE
) WITH (OIDS = FALSE);
