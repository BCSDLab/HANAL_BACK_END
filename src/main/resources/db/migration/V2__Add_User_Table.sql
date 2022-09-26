CREATE TABLE IF NOT EXISTS User (
    id          BIGINT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255)    NOT NULL,
    account_id  VARCHAR(255)    NOT NULL,
    password    VARCHAR(255)    NOT NULL,
    student_id  VARCHAR(255)    NOT NULL,
    department  INT             NOT NULL,
    user_type   VARCHAR(255)    NOT NULL,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    is_auth     TINYINT(1)      NOT NULL DEFAULT 0,
    is_deleted  TINYINT(1)      NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS User_Auth (
    id          BIGINT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT          NOT NULL,
    auth_num    VARCHAR(255)    NOT NULL,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    is_deleted  TINYINT(1)      NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);