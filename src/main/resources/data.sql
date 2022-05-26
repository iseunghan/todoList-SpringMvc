-- Account user, pass
INSERT INTO ACCOUNT (id, username, password, nickname, email) VALUES (1, 'admin', '$2y$10$BYXcyqENDa.uJDnNriO6VeB4aq9kOg9YvKgvH9Hfmrdux8TVubuMK', 'user_nick', 'admin@email.com');
INSERT INTO ACCOUNT_ROLES (account_id, roles) VALUES (1, 'ADMIN');
INSERT INTO ACCOUNT (id, username, password, nickname, email) VALUES (2, 'manager', '$2y$10$BYXcyqENDa.uJDnNriO6VeB4aq9kOg9YvKgvH9Hfmrdux8TVubuMK', 'user_nick', 'manager@email.com');
INSERT INTO ACCOUNT_ROLES (account_id, roles) VALUES (2, 'MANAGER');
INSERT INTO ACCOUNT (id, username, password, nickname, email) VALUES (3, 'user', '$2y$10$BYXcyqENDa.uJDnNriO6VeB4aq9kOg9YvKgvH9Hfmrdux8TVubuMK', 'user_nick', 'user@email.com');
INSERT INTO ACCOUNT_ROLES (account_id, roles) VALUES (3, 'USER');

-- TODO_ITEM
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NEVER', '오늘 해야 할 일을 적어보세요!', 1);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NEVER', '할일을 클릭하면 완료상태가 됩니다.', 1);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'DONE', '완료된 할일을 누르면 다시 이전 상태로 돌아갑니다.', 1);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NEVER', '✘를 누르면 할일이 삭제됩니다.', 1);

INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NEVER', '청소하기', 1);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'DONE', '빨래하기', 1);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NEVER', '공부하기', 1);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'DONE', '게임하기', 1);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NEVER', '청소하기', 1);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'DONE', '빨래하기', 1);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NEVER', '공부하기', 2);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'DONE', '게임하기', 2);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NEVER', '청소하기', 2);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'DONE', '빨래하기', 2);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NEVER', '공부하기', 2);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'DONE', '게임하기', 2);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NEVER', '청소하기', 2);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'DONE', '빨래하기', 2);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NEVER', '공부하기', 2);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'DONE', '게임하기', 2);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NEVER', '청소하기', 2);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'DONE', '빨래하기', 2);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NEVER', '공부하기', 2);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'DONE', '게임하기', 3);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NEVER', '청소하기', 3);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'DONE', '빨래하기', 3);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NEVER', '공부하기', 3);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'DONE', '게임하기', 3);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NEVER', '청소하기', 3);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'DONE', '빨래하기', 3);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NEVER', '공부하기', 3);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'DONE', '게임하기', 3);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NEVER', '청소하기', 3);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'DONE', '빨래하기', 3);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NEVER', '공부하기', 3);
INSERT INTO TODO_ITEM (created_at, updated_at, status, title, account_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'DONE', '게임하기', 3);