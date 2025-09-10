CREATE TABLE IF NOT EXISTS Posts(
    id bigserial PRIMARY KEY,
    post_name VARCHAR(256) NOT NULL,
    image_content_type VARCHAR(256) NOT NULL,
    image_payload BLOB NOT NULL,
    text VARCHAR(4096) NOT NULL,
    likes_count INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS Tags(
    id bigserial PRIMARY KEY,
    tag_name varchar(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS Comments(
    id bigserial PRIMARY KEY,
    post_id INTEGER NOT NULL REFERENCES Posts(id) ON DELETE CASCADE,
    text varchar(1024) NOT NULL
);

CREATE TABLE IF NOT EXISTS Tags_Posts(
    tag_id INTEGER NOT NULL REFERENCES Tags(id) ON DELETE CASCADE,
    post_id INTEGER NOT NULL REFERENCES Posts(id) ON DELETE CASCADE
);
