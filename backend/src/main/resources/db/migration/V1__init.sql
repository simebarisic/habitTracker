CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE habits (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name       VARCHAR(100) NOT NULL,
    icon       VARCHAR(10),
    sort_order INTEGER NOT NULL DEFAULT 0,
    active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_habit_user_name UNIQUE (user_id, name)
);

CREATE TABLE habit_logs (
    id         BIGSERIAL PRIMARY KEY,
    habit_id   BIGINT NOT NULL REFERENCES habits(id) ON DELETE CASCADE,
    log_date   DATE NOT NULL,
    completed  BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_habit_log_date UNIQUE (habit_id, log_date)
);

CREATE INDEX idx_habits_user_id ON habits(user_id);
CREATE INDEX idx_habit_logs_habit_id ON habit_logs(habit_id);
CREATE INDEX idx_habit_logs_log_date ON habit_logs(log_date);
