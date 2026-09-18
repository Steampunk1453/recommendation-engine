CREATE TABLE users (
    id VARCHAR(100) PRIMARY KEY,
    username VARCHAR(200) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE videos (
    id VARCHAR(100) PRIMARY KEY,
    title VARCHAR(300) NOT NULL,
    creator_id VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL,
    tags VARCHAR(2000),
    duration_seconds BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    popularity_score DOUBLE PRECISION NOT NULL,
    engagement_score DOUBLE PRECISION NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE user_interactions (
    id UUID PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL REFERENCES users(id),
    video_id VARCHAR(100) NOT NULL REFERENCES videos(id),
    type VARCHAR(20) NOT NULL CHECK (type IN ('VIEW','LIKE','DISLIKE','SHARE','COMMENT','SKIP')),
    watch_duration_seconds BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_videos_active_popularity ON videos(active, popularity_score DESC);
CREATE INDEX idx_videos_active_recency ON videos(active, created_at DESC);
CREATE INDEX idx_videos_category ON videos(category);
CREATE INDEX idx_interactions_user_time ON user_interactions(user_id, created_at DESC);
CREATE INDEX idx_interactions_video ON user_interactions(video_id);

INSERT INTO users (id, username, created_at) VALUES
('user-1', 'alex-rivera', DATEADD('DAY', -90, CURRENT_TIMESTAMP)),
('user-2', 'sam-chen', DATEADD('DAY', -60, CURRENT_TIMESTAMP));

INSERT INTO videos (id, title, creator_id, category, tags, duration_seconds, created_at,
                    popularity_score, engagement_score) VALUES
('video-architecture', 'Building Evolvable Systems', 'creator-mina-patel', 'technology', 'java,architecture,design', 1800, DATEADD('DAY', -3, CURRENT_TIMESTAMP), .72, .64),
('video-java26', 'What Is New in Java 26', 'creator-jordan-lee', 'technology', 'java,programming,tools', 1500, DATEADD('DAY', -8, CURRENT_TIMESTAMP), .82, .71),
('video-ai', 'Practical AI for Everyone', 'creator-noah-williams', 'science', 'ai,machine-learning,future', 2100, DATEADD('DAY', -2, CURRENT_TIMESTAMP), .88, .76),
('video-space', 'The New Space Race', 'creator-priya-singh', 'science', 'space,engineering,history', 2400, DATEADD('DAY', -20, CURRENT_TIMESTAMP), .65, .55),
('video-alps', 'A Week in the Alps', 'creator-luca-moretti', 'travel', 'nature,travel,photography', 2700, DATEADD('DAY', -1, CURRENT_TIMESTAMP), .61, .69),
('video-ancient', 'Ancient Cities Revisited', 'creator-sara-okafor', 'documentary', 'history,culture,archaeology', 1900, DATEADD('DAY', -12, CURRENT_TIMESTAMP), .52, .58),
('video-ocean', 'Life Beneath the Ocean', 'creator-elena-garcia', 'nature', 'nature,science,ocean', 2200, DATEADD('DAY', -5, CURRENT_TIMESTAMP), .74, .72),
('video-music', 'How Music Shapes Memory', 'creator-theo-martin', 'culture', 'music,science,psychology', 1600, DATEADD('DAY', -30, CURRENT_TIMESTAMP), .42, .51);

INSERT INTO user_interactions (id, user_id, video_id, type, watch_duration_seconds, created_at) VALUES
('00000000-0000-0000-0000-000000000001', 'user-1', 'video-architecture', 'LIKE', 1500, DATEADD('DAY', -1, CURRENT_TIMESTAMP)),
('00000000-0000-0000-0000-000000000002', 'user-1', 'video-java26', 'VIEW', 900, DATEADD('DAY', -2, CURRENT_TIMESTAMP)),
('00000000-0000-0000-0000-000000000003', 'user-2', 'video-ancient', 'LIKE', 1700, DATEADD('DAY', -3, CURRENT_TIMESTAMP));
