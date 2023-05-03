CREATE TABLE genres (
    id CHAR(32) PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6) NULL
);

CREATE TABLE genres_categories (
    genre_id CHAR(32) NOT NULL,
    category_id CHAR(32) NOT NULL,
    CONSTRAINT UN_GENRES_CATEGORIES UNIQUE(genre_id, category_id),
    CONSTRAINT FK_GENRE_ID FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE,
    CONSTRAINT FK_CATEGORY_ID FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE
);