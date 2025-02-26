CREATE TABLE IF NOT EXISTS "users" (
    "id"                UUID          NOT NULL PRIMARY KEY,
    "username"          VARCHAR(128)  NOT NULL UNIQUE,
    "password"          VARCHAR(1024) NULL,
    "version"           BIGINT        NOT NULL,
    "created_date_time" TIMESTAMP     NOT NULL,
    "updated_date_time" TIMESTAMP     NULL
);

CREATE TABLE IF NOT EXISTS "authorities" (
    "id"   SERIAL       NOT NULL PRIMARY KEY,
    "name" VARCHAR(128) NOT NULL UNIQUE

);

CREATE TABLE IF NOT EXISTS "users_have_authorities" (
    "user_id" UUID NOT NULL,
    "auth_id" INT  NOT NULL,

    CONSTRAINT "PK__users_have_authorities__user_id__auth_id"
        PRIMARY KEY ("user_id", "auth_id"),

    CONSTRAINT "FK__users_have_authorities__user_id"
        FOREIGN KEY ("user_id")
            REFERENCES "users" ("id")
            ON UPDATE CASCADE
            ON DELETE CASCADE,

    CONSTRAINT "FK__users_have_authorities__auth_id"
        FOREIGN KEY ("auth_id")
            REFERENCES "authorities" ("id")
            ON UPDATE CASCADE
            ON DELETE CASCADE
);

INSERT INTO "authorities" ("name")
VALUES ('ROLE_USER')
     , ('ROLE_ADMIN')
ON CONFLICT DO NOTHING;
