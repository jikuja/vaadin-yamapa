CREATE TABLE ITEMS
(
    ID INTEGER PRIMARY KEY NOT NULL IDENTITY,
    TITLE VARCHAR(80) DEFAULT '' NOT NULL,
    DESCRIPTION VARCHAR(1024) DEFAULT '' NOT NULL,
    OPTLOCK INTEGER DEFAULT 1 NOT NULL,
    USER_ID INTEGER DEFAULT NULL,
    LAT DOUBLE DEFAULT 0.0E0 NOT NULL,
    LONG DOUBLE DEFAULT 0.0E0 NOT NULL
);
CREATE TABLE USERS
(
    ID INTEGER PRIMARY KEY NOT NULL IDENTITY,
    NAME VARCHAR(256) DEFAULT '' NOT NULL,
    PASSWORD VARCHAR(256) DEFAULT '',
    OAUTH VARCHAR(165) DEFAULT NULL,
    EXTERNAL_ID VARCHAR(128) DEFAULT NULL,
    OPTLOCK INTEGER DEFAULT 1 NOT NULL
);
