�� CÁC CHỨC NĂNG CHÍNH
1. Authentication & Authorization

   ✅ Đăng ký/Đăng nhập với email/password

   ✅ JWT Token authentication

   ✅ Password reset qua email

   ✅ Logout với token invalidation

   ✅ BCrypt password encryption
2. User Management

   ✅ User profile với avatar upload

   ✅ Friend system (gửi/chấp nhận/từ chối lời mời kết bạn)

   ✅ User search theo email

   ✅ Profile update (display name, avatar)

3. Conversation System

   ✅ 1-1 conversations (chat riêng tư)

   ✅ Group conversations (chat nhóm)

   ✅ Conversation management (tạo, thêm/xóa thành viên)

   ✅ Conversation archiving

4. Messaging Features

   ✅ Real-time messaging qua WebSocket

   ✅ Multiple message types: TEXT, IMAGE, VIDEO, FILE

   ✅ Message status: SENT, DELIVERED, SEEN

   ✅ Message actions: Edit, Recall, Reply

   ✅ File attachments với size validation

   ✅ Message search theo keyword

   ✅ Pagination cho message history

5. Real-time Features

   ✅ WebSocket cho instant messaging

   ✅ Push notifications khi có tin nhắn mới

   ✅ Online/offline status tracking

   ✅ Message delivery status real-time

6. File Management

   ✅ Avatar upload cho user profile

   ✅ Media upload (images, videos, files)

   ✅ File size validation (max 100MB cho video)

   ✅ Organized storage (avatar/, image/, video/, file/)

7. Notification System

   ✅ Email notifications cho password reset

   ✅ In-app notifications cho friend requests, messages

   ✅ Notification status (read/unread)

�� KIẾN THỨC KỸ THUẬT ÁP DỤNG
1. Spring Framework

   Spring Boot auto-configuration

   Spring Security với custom authentication

   Spring Data JPA với Hibernate

   Spring WebSocket với STOMP protocol

   Spring Mail cho email service

2. Security & Authentication

   JWT (JSON Web Token) cho stateless authentication

   BCrypt password hashing

   Spring Security với custom UserDetailsService

   Redis cho token storage và session management

3. Real-time Communication

   WebSocket với STOMP protocol

   Message broker cho pub/sub pattern

   Channel interceptors cho authentication

   SimpMessagingTemplate cho message routing

4. Database & Caching

   MySQL với JPA/Hibernate

   Redis cho caching và session

   Database relationships (One-to-Many, Many-to-Many)

   Transaction management

5. File Handling

   MultipartFile processing

   File upload với validation

   Organized file storage structure

   Content-type detection

6. API Design

   RESTful API design

   DTO pattern cho request/response

   Global exception handling

   API documentation với OpenAPI/Swagger

7. DevOps & Deployment

   Docker containerization

   Docker Compose cho multi-service setup

   Environment variables management

   Maven build system