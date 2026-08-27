-- Default account seeded with data imported from the user's Notion "Habits" database.
-- Change the username/email/password from the app's Profile settings screen after first login.
INSERT INTO users (username, email, password_hash)
VALUES ('sime', 'sime@example.com', '$2b$10$b6ZflOXBxqJYXTk7AyIL4.Iy.CAxXzapEvCUWFpR0crQWY5oQK8a2');

INSERT INTO habits (user_id, name, sort_order, created_at)
SELECT (SELECT id FROM users WHERE username = 'sime'), name, sort_order, TIMESTAMP '2024-11-17 00:00:00'
FROM (VALUES
    ('Workout', 1),
    ('Meditation', 2),
    ('Journaling', 3),
    ('Reading', 4),
    ('Hydration', 5),
    ('Morning read', 6),
    ('Morning hygiene', 7),
    ('Unprocessed food diet', 8),
    ('Screen time', 9),
    ('Sunlight exposure', 10),
    ('Morning walk with puppy', 11),
    ('8hrs of sleep', 12),
    ('Make bed', 13),
    ('10k Steps', 14),
    ('Cold water', 15),
    ('Snoozing', 16),
    ('Grouding', 17),
    ('Weight', 18)
) AS t(name, sort_order);
