CREATE
DATABASE message;
USE
message;
-- Tạo bảng người dùng
CREATE TABLE users
(
    id           CHAR(36) PRIMARY KEY,                  -- UUID của người dùng
    username     VARCHAR(50) UNIQUE NOT NULL,           -- Tên đăng nhập (unique, không null)
    password     varchar(200),
    display_name VARCHAR(100),                          -- Tên hiển thị
    avatar_url   TEXT,                                  -- Đường dẫn ảnh đại diện
    phone_number VARCHAR(20) UNIQUE,                    -- Số điện thoại (unique)
    email        VARCHAR(100) UNIQUE,                   -- Email (unique)
    created_at   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP, -- Thời gian tạo tài khoản
    updated_at   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP, -- Thời gian cập nhật tài khoản
    status       VARCHAR(20) DEFAULT 'active',          -- Trạng thái tài khoản
    last_login   TIMESTAMP,                             -- Thời gian đăng nhập lần cuối
    INDEX (username),                                   -- Chỉ mục cho tên người dùng
    INDEX (phone_number),                               -- Chỉ mục cho số điện thoại
    INDEX (email)                                       -- Chỉ mục cho email
);
-- Tạo bảng kết bạn
CREATE TABLE friendships
(
    user_id      CHAR(36),                              -- Người gửi lời mời
    friend_id    CHAR(36),                              -- Người nhận lời mời
    status       VARCHAR(20) DEFAULT 'pending',         -- Trạng thái: pending, accepted, blocked
    requested_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP, -- Thời điểm gửi lời mời
    accepted_at  TIMESTAMP,                             -- Thời điểm chấp nhận lời mời
    PRIMARY KEY (user_id, friend_id),                   -- Cặp bạn bè duy nhất
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX (status)                                      -- Chỉ mục cho trạng thái kết bạn
);
-- Tạo bảng cuộc trò chuyện
CREATE TABLE conversations
(
    id              CHAR(36) PRIMARY KEY,                -- UUID cuộc trò chuyện
    is_group        BOOLEAN   DEFAULT FALSE,             -- Cuộc trò chuyện nhóm hay cá nhân
    name            VARCHAR(100),                        -- Tên cuộc trò chuyện (chỉ dành cho nhóm)
    created_by      CHAR(36),                            -- Người tạo cuộc trò chuyện
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Thời gian tạo
    last_message_at TIMESTAMP,                           -- Thời gian tin nhắn cuối cùng
    is_archived     BOOLEAN   DEFAULT FALSE,             -- Trạng thái lưu trữ cuộc trò chuyện
    FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE CASCADE,
    INDEX (created_by)                                   -- Chỉ mục cho người tạo cuộc trò chuyện
);
-- Tạo bảng thành viên trong cuộc trò chuyện
CREATE TABLE conversation_members
(
    conversation_id CHAR(36),                              -- ID cuộc trò chuyện
    user_id         CHAR(36),                              -- ID thành viên
    joined_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP, -- Thời gian tham gia
    role            VARCHAR(20) DEFAULT 'member',          -- Vai trò thành viên
    PRIMARY KEY (conversation_id, user_id),                -- Cặp duy nhất cho mỗi thành viên
    FOREIGN KEY (conversation_id) REFERENCES conversations (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX (role)                                           -- Chỉ mục cho vai trò thành viên
);
-- Tạo bảng tin nhắn
CREATE TABLE messages
(
    id              CHAR(36) PRIMARY KEY,                  -- ID tin nhắn
    conversation_id CHAR(36),                              -- ID cuộc trò chuyện
    sender_id       CHAR(36),                              -- ID người gửi
    content         TEXT,                                  -- Nội dung tin nhắn
    message_type    VARCHAR(20) DEFAULT 'text',            -- Loại tin nhắn (text, image, file...)
    created_at      TIMESTAMP   DEFAULT CURRENT_TIMESTAMP, -- Thời gian gửi tin nhắn
    reply_to        CHAR(36),                              -- Tin nhắn trả lời
    is_edited       BOOLEAN     DEFAULT FALSE,             -- Tin nhắn đã chỉnh sửa hay chưa
    status          VARCHAR(20) DEFAULT 'sent',            -- Trạng thái tin nhắn: sent, delivered, seen, failed
    FOREIGN KEY (conversation_id) REFERENCES conversations (id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (reply_to) REFERENCES messages (id) ON DELETE CASCADE,
    INDEX (conversation_id),                               -- Chỉ mục cho cuộc trò chuyện
    INDEX (sender_id)                                      -- Chỉ mục cho người gửi
);
-- Tạo bảng trạng thái tin nhắn
CREATE TABLE message_status
(
    message_id CHAR(36),                              -- ID tin nhắn
    user_id    CHAR(36),                              -- Người xem
    status     VARCHAR(20) DEFAULT 'delivered',       -- Trạng thái tin nhắn
    updated_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP, -- Thời gian cập nhật trạng thái
    PRIMARY KEY (message_id, user_id),
    FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX (status)                                    -- Chỉ mục cho trạng thái tin nhắn
);
-- Tạo bảng file đính kèm
CREATE TABLE attachments
(
    id         CHAR(36) PRIMARY KEY, -- ID file đính kèm
    message_id CHAR(36),             -- Tin nhắn liên kết với file
    file_url   TEXT NOT NULL,        -- URL của file (lưu trữ trên cloud)
    file_type  VARCHAR(50),          -- Loại file (image, video, etc.)
    file_size  BIGINT,               -- Kích thước file (bytes)
    FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
    INDEX (file_type)                -- Chỉ mục cho loại file
);
-- Tạo bảng thông báo
CREATE TABLE notifications
(
    id         CHAR(36) PRIMARY KEY,                -- ID thông báo
    user_id    CHAR(36),                            -- Người nhận thông báo
    type       VARCHAR(50),                         -- Loại thông báo (message, friend_request, etc.)
    content    TEXT,                                -- Nội dung thông báo
    extra_data JSON,                                -- Dữ liệu bổ sung (ví dụ: id cuộc trò chuyện, id tin nhắn)
    is_read    BOOLEAN   DEFAULT FALSE,             -- Đã đọc chưa
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Thời gian tạo thông báo
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX (is_read)                                 -- Chỉ mục cho trạng thái đọc
);



