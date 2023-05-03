CREATE TABLE videos_video_media (
    id CHAR(32) PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    checksum VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    encoded_path VARCHAR(500) NOT NULL,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE videos_image_media (
    id CHAR(32) PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    checksum VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL
);

CREATE TABLE videos (
    id CHAR(32) PRIMARY KEY NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    year_launched SMALLINT NOT NULL,
    opened BOOLEAN NOT NULL DEFAULT FALSE,
    published BOOLEAN NOT NULL DEFAULT FALSE,
    rating VARCHAR(5) NOT NULL,
    duration DECIMAL(5, 2) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    video_id CHAR(32) NULL,
    trailer_id CHAR(32) NULL,
    banner_id CHAR(32) NULL,
    thumbnail_id CHAR(32) NULL,
    thumbnail_half_id CHAR(32) NULL,
    CONSTRAINT FK_VIDEOS_VIDEO_MEDIA_ID FOREIGN KEY (video_id) REFERENCES videos_video_media (id) ON DELETE CASCADE,
    CONSTRAINT FK_VIDEOS_TRAILER_MEDIA_ID FOREIGN KEY (trailer_id) REFERENCES videos_video_media (id) ON DELETE CASCADE,
    CONSTRAINT FK_VIDEOS_BANNER_MEDIA_ID FOREIGN KEY (banner_id) REFERENCES videos_image_media (id) ON DELETE CASCADE,
    CONSTRAINT FK_VIDEOS_THUMBNAIL_MEDIA_ID FOREIGN KEY (thumbnail_id) REFERENCES videos_image_media (id) ON DELETE CASCADE,
    CONSTRAINT FK_VIDEOS_THUMBNAIL_HALF_MEDIA_ID FOREIGN KEY (thumbnail_half_id) REFERENCES videos_image_media (id) ON DELETE CASCADE
);

CREATE TABLE videos_categories (
    video_id CHAR(32) NOT NULL,
    category_id CHAR(32) NOT NULL,
    CONSTRAINT UN_VIDEOS_CATEGORIES UNIQUE (video_id, category_id),
    CONSTRAINT FK_VIDEOS_CATEGORIES_VIDEO_ID FOREIGN KEY (video_id) REFERENCES videos (id) ON DELETE CASCADE,
    CONSTRAINT FK_VIDEOS_CATEGORIES_CATEGORY_ID FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE
);

CREATE TABLE videos_genres (
    video_id CHAR(32) NOT NULL,
    genres_id CHAR(32) NOT NULL,
    CONSTRAINT UN_VIDEOS_GENRES UNIQUE (video_id, genres_id),
    CONSTRAINT FK_VIDEOS_GENRES_VIDEO_ID FOREIGN KEY (video_id) REFERENCES videos (id) ON DELETE CASCADE,
    CONSTRAINT FK_VIDEOS_GENRES_GENRE_ID FOREIGN KEY (genres_id) REFERENCES genres (id) ON DELETE CASCADE
);

CREATE TABLE videos_cast_members (
    video_id CHAR(32) NOT NULL,
    cast_member_id CHAR(32) NOT NULL,
    CONSTRAINT UN_VIDEOS_CAST_MEMBERS UNIQUE (video_id, cast_member_id),
    CONSTRAINT FK_VIDEOS_CAST_MEMBERS_VIDEO_ID FOREIGN KEY (video_id) REFERENCES videos (id) ON DELETE CASCADE,
    CONSTRAINT FK_VIDEOS_CAST_MEMBERS_CAST_MEMBER_ID FOREIGN KEY (cast_member_id) REFERENCES cast_members (id) ON DELETE CASCADE
);