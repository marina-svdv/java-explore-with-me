DROP TABLE IF EXISTS compilation_events CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS locations CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS subscriptions CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(254) NOT NULL UNIQUE,
    name VARCHAR(250) NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS locations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    lat DOUBLE PRECISION NOT NULL,
    lon DOUBLE PRECISION NOT NULL,
    CONSTRAINT unique_location UNIQUE (lat, lon)
);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation VARCHAR(2000) NOT NULL,
    category_id BIGINT NOT NULL,
    description TEXT NOT NULL,
    event_date TIMESTAMP NOT NULL,
    location_id BIGINT NOT NULL,
    paid BOOLEAN DEFAULT FALSE,
    participant_limit INTEGER NOT NULL DEFAULT 0,
    request_moderation BOOLEAN NOT NULL DEFAULT TRUE,
    state VARCHAR(20) NOT NULL,
    title VARCHAR(120) NOT NULL,
    initiator_id BIGINT NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_on TIMESTAMP,
    views BIGINT NOT NULL DEFAULT 0,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (initiator_id) REFERENCES users(id),
    FOREIGN KEY (location_id) REFERENCES locations(id)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    event_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(id),
    FOREIGN KEY (requester_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN NOT NULL DEFAULT FALSE,
    title VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS compilation_events (
    compilation_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    PRIMARY KEY (compilation_id, event_id),
    FOREIGN KEY (compilation_id) REFERENCES compilations(id),
    FOREIGN KEY (event_id) REFERENCES events(id)
);

CREATE TABLE IF NOT EXISTS subscriptions (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (follower_id) REFERENCES users(id),
    FOREIGN KEY (following_id) REFERENCES users(id),
    UNIQUE (follower_id, following_id)
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    message VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (follower_id) REFERENCES users(id),
    FOREIGN KEY (following_id) REFERENCES users(id)
);

-- индексы для ускорения поиска
CREATE INDEX IF NOT EXISTS idx_event_date ON events(event_date);
CREATE INDEX IF NOT EXISTS idx_event_state ON events(state);
CREATE INDEX IF NOT EXISTS idx_request_status ON requests(status);
CREATE INDEX IF NOT EXISTS idx_compilation_pinned ON compilations(pinned);