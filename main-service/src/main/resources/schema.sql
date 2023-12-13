CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY ,
    name VARCHAR(200),
    email VARCHAR(200),
    CONSTRAINT uq_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories
(
    category_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY ,
    name VARCHAR(50) NOT NULL ,
    CONSTRAINT uq_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS locations
(
    location_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY ,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS events
(
    event_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY ,
    annotation VARCHAR(2000) NOT NULL ,
    category_id BIGINT NOT NULL,
    created_on TIMESTAMP WITHOUT TIME ZONE,
    description VARCHAR(7000) NOT NULL ,
    event_date TIMESTAMP WITHOUT TIME ZONE,
    initiator_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    paid BOOLEAN NOT NULL,
    participant_limit BIGINT NOT NULL ,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN NOT NULL ,
    state VARCHAR(20) NOT NULL ,
    title VARCHAR(120) NOT NULL,
    CONSTRAINT fk_event_to_categories FOREIGN KEY (category_id)
        REFERENCES categories (category_id),
    CONSTRAINT fk_event_to_users FOREIGN KEY (initiator_id)
        REFERENCES users (user_id) ON UPDATE RESTRICT ON DELETE CASCADE,
    CONSTRAINT fk_event_to_locations FOREIGN KEY (location_id)
        REFERENCES locations (location_id) ON UPDATE RESTRICT ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests
(
    request_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY ,
    created TIMESTAMP WITHOUT TIME ZONE,
    event_id BIGINT NOT NULL,
    requester_id BIGINT,
    status VARCHAR(20),
    CONSTRAINT fk_participation_request_event FOREIGN KEY (event_id) REFERENCES events (event_id),
    CONSTRAINT fk_participation_request_requester FOREIGN KEY (requester_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS compilations
(
    compilation_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY ,
    title VARCHAR(50),
    pinned BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS events_compilations
(
    event_id BIGINT NOT NULL,
    compilation_id BIGINT NOT NULL,
    PRIMARY KEY (event_id, compilation_id),
    CONSTRAINT fk_compilation_id FOREIGN KEY (compilation_id) REFERENCES compilations (compilation_id),
    CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES events (event_id)
);