DROP TABLE IF EXISTS hits;

CREATE TABLE  IF NOT EXISTS hits (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
  app VARCHAR(32) NOT NULL,
  uri VARCHAR(128),
  ip VARCHAR(16) NOT NULL,
  created timestamp WITHOUT TIME ZONE NOT NULL
);