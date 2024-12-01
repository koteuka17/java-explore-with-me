DROP TABLE IF EXISTS users, categories, events, requests, compilations, events_compilations cascade;

CREATE TABLE IF NOT EXISTS users(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (user_id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories(
    category_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (category_id),
    CONSTRAINT UQ_CATEGORY_NAME UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS events(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation VARCHAR(2000) NOT NULL,
    category_id BIGINT NOT NULL,
    confirmed_requests BIGINT,
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description VARCHAR(7000) NOT NULL,
    date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id BIGINT NOT NULL,
    lat REAL NOT NULL,
    lon REAL NOT NULL,
    paid BOOL NOT NULL,
    participant_limit  BIGINT,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOL,
    state VARCHAR(32) NOT NULL,
    title VARCHAR(120) NOT NULL,
    CONSTRAINT pk_event PRIMARY KEY (id),
    FOREIGN KEY (category_id)
        REFERENCES categories (category_id) ON DELETE CASCADE,
    FOREIGN KEY (initiator_id)
        REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status VARCHAR(10) NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT UQ_EVENT_WITH_REQUESTER UNIQUE (event_id, requester_id),
    FOREIGN KEY (event_id)
        REFERENCES events (id) ON DELETE CASCADE,
    FOREIGN KEY (requester_id)
        REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS compilations(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title VARCHAR(50) NOT NULL,
    pinned BOOL NOT NULL,
    CONSTRAINT pk_compilations PRIMARY KEY (id),
    CONSTRAINT UQ_COMPILATIONS_TITLE UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS events_compilations(
    event_id BIGINT REFERENCES events (id) ON DELETE CASCADE,
    compilation_id BIGINT REFERENCES compilations (id) ON DELETE CASCADE,
    PRIMARY KEY (event_id, compilation_id),
    CONSTRAINT UQ_EVENT_WITH_compilation UNIQUE (event_id, compilation_id)
);