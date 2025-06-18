
document.addEventListener('DOMContentLoaded', () => {
    // Toggle profile overlay
    const toggleProfileBtn = document.getElementById('toggleProfileBtn');
    if (toggleProfileBtn) {
        toggleProfileBtn.addEventListener('click', () => {
            const overlay = document.getElementById('profileOverlay');
            if (overlay) overlay.classList.toggle('active');
        });
    }

    // Đóng overlay khi click ra ngoài
    const profileOverlay = document.getElementById('profileOverlay');
    if (profileOverlay) {
        profileOverlay.addEventListener('click', (e) => {
            if (e.target === e.currentTarget) {
                e.currentTarget.classList.remove('active');
            }
        });
    }

    // Hiển thị nội dung trò chuyện
    document.querySelectorAll('.group-item').forEach(item => {
        item.addEventListener('click', () => {
            // Xóa active class khỏi tất cả item
            document.querySelectorAll('.group-item').forEach(i => i.classList.remove('active'));
            // Thêm active class cho item được click
            item.classList.add('active');

            const group = item.getAttribute('data-group');
            const message = item.getAttribute('data-message');

            const chatContent = document.getElementById('chatContent');
            if (chatContent) {
                chatContent.innerHTML = `
<div class="flex flex-col h-full">
    <!-- Chat Header -->
<div class="chat-header p-4 flex items-center justify-between">
    <div class="flex items-center">
    <img src="https://via.placeholder.com/40" class="w-10 h-10 rounded-full mr-3" alt="Group Avatar">
    <div>
    <h2 class="text-xl font-bold text-gray-800">${group}</h2>
<p class="text-sm text-gray-500">3 thành viên, 2 online</p>
</div>
</div>
<div class="flex items-center space-x-4">
    <button class="text-gray-600 hover:text-blue-600 transition" title="Gọi thoại">
        <i class="fas fa-phone-alt text-lg"></i>
    </button>
    <button class="text-gray-600 hover:text-blue-600 transition" title="Gọi video">
        <i class="fas fa-video text-lg"></i>
    </button>
    <button class="text-gray-600 hover:text-blue-600 transition" title="Tùy chọn">
        <i class="fas fa-ellipsis-v text-lg"></i>
    </button>
</div>
</div>

<!-- Nội dung tin nhắn -->
<div class="flex-grow overflow-y-auto space-y-4 p-6">
    <!-- Tin nhắn người khác -->
    <div class="message-container flex items-start space-x-3">
        <img src="https://via.placeholder.com/32" class="w-8 h-8 rounded-full" />
        <div>
            <div class="message-bubble bg-gray-100 px-4 py-2 rounded-lg shadow-sm max-w-md">
                <p class="text-gray-800">Chào cả nhóm, mai Sion nhắn tối nay nhé!</p>
            </div>
            <div class="text-xs text-gray-400 mt-1">Sion · 14:21</div>
            <div class="message-options absolute right-2 top-2">
                <button class="text-gray-500 hover:text-gray-700"><i class="fas fa-ellipsis-h"></i></button>
            </div>
        </div>
    </div>
    <!-- Tin nhắn ảnh -->
    <div class="message-container flex items-start space-x-3">
        <img src="https://via.placeholder.com/32" class="w-8 h-8 rounded-full" />
        <div>
            <div class="message-bubble bg-gray-100 px-4 py-2 rounded-lg shadow-sm max-w-md">
                <img src="https://via.placeholder.com/150" class="rounded-lg max-w-xs" alt="Sent Image"/>
                <p class="text-gray-800 mt-2">Đây là sơ đồ thiết kế nhé!</p>
            </div>
            <div class="text-xs text-gray-400 mt-1">Trang · 14:22</div>
            <div class="message-options absolute right-2 top-2">
                <button class="text-gray-500 hover:text-gray-700"><i class="fas fa-ellipsis-h"></i></button>
            </div>
        </div>
    </div>
    <!-- Tin nhắn của bạn -->
    <div class="message-container flex justify-end">
        <div>
            <div class="message-bubble bg-blue-500 text-white px-4 py-2 rounded-lg shadow-md max-w-md">
                <p>${message}</p>
            </div>
            <div class="text-xs text-right text-gray-400 mt-1">Bạn · 14:23 <i class="fas fa-check-double text-blue-400 ml-1"></i></div>
            <div class="message-options absolute right-2 top-2">
                <button class="text-white hover:text-gray-200"><i class="fas fa-ellipsis-h"></i></button>
            </div>
        </div>
    </div>
</div>

<!-- Hộp nhập tin nhắn -->
<div class="p-4 border-t bg-white">
    <div class="flex gap-2">
        <input type="text" class="chat-input flex-grow p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" placeholder="Nhập tin nhắn...">
            <button class="send-button px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition">Gửi</button>
    </div>
</div>
</div>
`}