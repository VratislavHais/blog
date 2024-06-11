INSERT INTO category (name) VALUES ('Technology');
INSERT INTO category (name) VALUES ('Health');
INSERT INTO category (name) VALUES ('Education');
INSERT INTO category (name) VALUES ('Lifestyle');
INSERT INTO category (name) VALUES ('Finance');

INSERT INTO user_table (email, username, password, full_name, role) VALUES (
    'test@test.cz',
    'vhais',
    '$2a$12$X5su42X7E8jAEDe526nMdusjePL/fuA5GxUqQz4UsW.0zhHv/npNm',
    'Vratislav Hais',
    'ROLE_ADMIN'
);

INSERT INTO user_table (email, username, password, full_name, role) VALUES (
    'test@test2.cz',
    'test',
    '$2a$12$X5su42X7E8jAEDe526nMdusjePL/fuA5GxUqQz4UsW.0zhHv/npNm',
    'Vratislav Hais',
    'ROLE_USER'
);

INSERT INTO tag (name) VALUES ('first');

SET @userID = (SELECT id FROM user_table WHERE username = 'vhais');
SET @categoryId = (SELECT id FROM category WHERE name = 'Technology');

INSERT INTO post (title, content, created_at, updated_at, author_id, category_id) VALUES (
    'Test title',
    'This will be replaced with a large content',
    '2024-05-27T12:30:00',
    NOW(),
    @userId,
    @categoryId
);

INSERT INTO post_tags (post_id, tag_id) VALUES (
    (SELECT id FROM post WHERE title = 'Test title'),
    (SELECT id FROM tag WHERE name = 'first')
);
