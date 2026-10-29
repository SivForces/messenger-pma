CREATE TABLE users (
                       id            BIGSERIAL PRIMARY KEY,
                       username      VARCHAR(32)  NOT NULL UNIQUE,
                       display_name  VARCHAR(64)  NOT NULL,
                       password_hash VARCHAR(100) NOT NULL,
                       phone         VARCHAR(20),
                       bio           VARCHAR(300),
                       birth_date    DATE,
                       avatar_url    VARCHAR(255),
                       show_phone      BOOLEAN NOT NULL DEFAULT FALSE,
                       show_birth_date BOOLEAN NOT NULL DEFAULT TRUE,
                       show_bio        BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE chats (
                       id         BIGSERIAL PRIMARY KEY,
                       type       VARCHAR(10) NOT NULL DEFAULT 'direct',
                       created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE chat_members (
                              chat_id       BIGINT NOT NULL REFERENCES chats(id) ON DELETE CASCADE,
                              user_id       BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                              muted_until   TIMESTAMPTZ,
                              last_read_at  TIMESTAMPTZ,
                              PRIMARY KEY (chat_id, user_id)
);

CREATE TABLE messages (
                          id         BIGSERIAL PRIMARY KEY,
                          chat_id    BIGINT NOT NULL REFERENCES chats(id) ON DELETE CASCADE,
                          sender_id  BIGINT NOT NULL REFERENCES users(id),
                          type       VARCHAR(10) NOT NULL DEFAULT 'text',
                          text       TEXT,
                          voice_url  VARCHAR(255),
                          duration   INT,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_messages_chat_created ON messages(chat_id, created_at DESC);