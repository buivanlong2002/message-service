document.addEventListener('DOMContentLoaded', async () => {
    // Hàm tải nội dung từ file HTML
    async function loadComponent(url, containerId) {
        try {
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(`Không thể tải file: ${url}`);
            }
            const content = await response.text();
            document.getElementById(containerId).innerHTML = content;
        } catch (error) {
            console.error(`Lỗi khi tải ${url}:`, error);
        }
    }

    // Tải các thành phần từ thư mục templates/fragments
    await Promise.all([
        loadComponent('/templates/fragments/Sidebar.html', 'sidebarContainer'),
        loadComponent('/templates/fragments/ChatContent.html', 'chatContentContainer'),
        loadComponent('/templates/fragments/ProfileOverlay.html', 'profileOverlayContainer')
    ]);

    // Toggle profile overlay khi click vào nút
    const toggleProfileBtn = document.getElementById('toggleProfileBtn');
    if (toggleProfileBtn) {
        toggleProfileBtn.addEventListener('click', () => {
            const overlay = document.getElementById('menuOverlay');
            if (overlay) {
                overlay.classList.toggle('active');
            }
        });
    }

    // Đóng overlay khi click ra ngoài (click vào phần nền của overlay)
    const menuOverlay = document.getElementById('menuOverlay');
    if (menuOverlay) {
        menuOverlay.addEventListener('click', (e) => {
            if (e.target === e.currentTarget) {
                e.currentTarget.classList.remove('active');
            }
        });
    }

    // Hiển thị nội dung trò chuyện khi click vào nhóm
    const groupItems = document.querySelectorAll('.group-item');
    groupItems.forEach(item => {
        item.addEventListener('click', () => {
            // Deselect tất cả các nhóm và chọn nhóm vừa click
            groupItems.forEach(i => i.classList.remove('active'));
            item.classList.add('active');

            const group = item.getAttribute('data-group');
            const message = item.getAttribute('data-message');

            // Cập nhật nội dung chat
            const chatContent = document.getElementById('chatContent');
            if (chatContent) {
                chatContent.innerHTML = `
                    <div class="flex flex-col h-full">
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

                        <div class="flex-grow overflow-y-auto space-y-4 p-6">
                            <div class="message-container flex items-start space-x-3">
                                <img src="https://via.placeholder.com/32" class="w-8 h-8 rounded-full"/>
                                <div>
                                    <div class="message-bubble bg-gray-100 px-4 py-2 rounded-lg shadow-sm max-w-md">
                                        <p class="text-gray-800">Chào cả nhóm, mai Sion nhắn tối nay nhé!</p>
                                    </div>
                                    <div class="text-xs text-gray-400 mt-1">Sion · 14:21</div>
                                </div>
                            </div>

                            <div class="message-container flex items-start space-x-3">
                                <img src="https://via.placeholder.com/32" class="w-8 h-8 rounded-full"/>
                                <div>
                                    <div class="message-bubble bg-gray-100 px-4 py-2 rounded-lg shadow-sm max-w-md">
                                        <img src="https://via.placeholder.com/150" class="rounded-lg max-w-xs" alt="Sent Image"/>
                                        <p class="text-gray-800 mt-2">Đây là sơ đồ thiết kế nhé!</p>
                                    </div>
                                    <div class="text-xs text-gray-400 mt-1">Trang · 14:22</div>
                                </div>
                            </div>

                            <div class="message-container flex justify-end">
                                <div>
                                    <div class="message-bubble bg-blue-500 text-white px-4 py-2 rounded-lg shadow-md max-w-md">
                                        <p>${message}</p>
                                    </div>
                                    <div class="text-xs text-right text-gray-400 mt-1">Bạn · 14:23 <i class="fas fa-check-double text-blue-400 ml-1"></i></div>
                                </div>
                            </div>
                        </div>

                        <div class="p-4 border-t bg-white">
                            <div class="flex gap-2">
                                <input type="text" class="chat-input flex-grow p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" placeholder="Nhập tin nhắn...">
                                <button class="send-button px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition">Gửi</button>
                            </div>
                        </div>
                    </div>
                `;
            }
        });
    });
});
