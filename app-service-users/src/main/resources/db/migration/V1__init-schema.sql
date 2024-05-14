CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Create tables

CREATE TABLE IF NOT EXISTS "users"
(
    "id"                UUID          NOT NULL PRIMARY KEY,
    "username"          VARCHAR(128)  NOT NULL UNIQUE,
    "password"          VARCHAR(1024) NULL,
    "version"           BIGINT        NOT NULL,
    "created_date_time" TIMESTAMP     NOT NULL,
    "updated_date_time" TIMESTAMP     NULL
) WITH (OIDS = FALSE);

CREATE TABLE IF NOT EXISTS "authorities"
(
    "id"   UUID         NOT NULL PRIMARY KEY,
    "name" VARCHAR(128) NOT NULL UNIQUE

) WITH (OIDS = FALSE);

CREATE TABLE IF NOT EXISTS "users_has_authorities"
(
    "user_id" UUID NOT NULL,
    "auth_id" UUID NOT NULL,

    CONSTRAINT "UQ__users_has_authorities__user_id__auth_id"
        UNIQUE ("user_id", "auth_id"),

    CONSTRAINT "FK__users_has_authorities__users"
        FOREIGN KEY ("user_id")
            REFERENCES "users" ("id")
            ON UPDATE CASCADE
            ON DELETE CASCADE,

    CONSTRAINT "FK__users_has_authorities__authorities"
        FOREIGN KEY ("auth_id")
            REFERENCES "authorities" ("id")
            ON UPDATE CASCADE
            ON DELETE CASCADE
) WITH (OIDS = FALSE);

-- Create initial data

INSERT INTO "authorities" ("id", "name") VALUES (gen_random_uuid(), "ROLE_USER");
