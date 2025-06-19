// js/chat.js
export function initChat() {
    document.querySelectorAll('.group-item').forEach(item => {
        item.addEventListener('click', () => {
            document.querySelectorAll('.group-item').forEach(i => i.classList.remove('active'));
            item.classList.add('active');

            const group = item.getAttribute('data-group');
            const message = item.getAttribute('data-message');

            const chatContent = document.getElementById('chatContent');
            chatContent.innerHTML = `
                <div class="chat-main flex flex-col h-full flex-grow">
                    <div class="chat-header flex items-center justify-between p-4 bg-white dark:bg-gray-900 border-b border-gray-200 dark:border-gray-700">
                        <div class="flex items-center">
                            <img src="https://via.placeholder.com/40" class="w-10 h-10 rounded-full mr-3 avatar" alt="Group Avatar">
                            <div>
                                <h2 class="text-xl font-bold text-gray-800 dark:text-gray-100">${group}</h2>
                                <p class="text-sm text-gray-500 dark:text-gray-400">3 thành viên, 2 online</p>
                            </div>
                        </div>
                        <div class="flex items-center space-x-4">
                            <button class="action-button text-gray-600 dark:text-gray-300 hover:text-black dark:hover:text-white" title="Gọi thoại">
                                <i class="fas fa-phone-alt text-lg"></i>
                            </button>
                            <button class="action-button text-gray-600 dark:text-gray-300 hover:text-black dark:hover:text-white" title="Gọi video">
                                <i class="fas fa-video text-lg"></i>
                            </button>
                            <button class="action-button text-gray-600 dark:text-gray-300 hover:text-black dark:hover:text-white" title="Tùy chọn" id="toggleInfoBtn">
                                <i class="fas fa-ellipsis-v text-lg"></i>
                            </button>
                        </div>
                    </div>
                    <div class="flex-grow overflow-y-auto space-y-4 p-6 bg-gray-50 dark:bg-gray-800">
                        <div class="message-container flex items-start space-x-3">
                            <img src="https://via.placeholder.com/32" class="w-8 h-8 rounded-full avatar" alt="Avatar"/>
                            <div class="relative">
                                <div class="message-bubble bg-gray-100 dark:bg-gray-700 px-4 py-3 rounded-lg shadow-sm max-w-md">
                                    <p class="text-gray-800 dark:text-gray-200">Chào cả nhóm, mai Sion nhắn tối nay nhé!</p>
                                </div>
                                <div class="text-xs text-gray-400 dark:text-gray-500 mt-1">Sion · 14:21</div>
                            </div>
                        </div>
                        <div class="message-container flex items-start space-x-3">
                            <img src="https://via.placeholder.com/32" class="w-8 h-8 rounded-full avatar" alt="Avatar"/>
                            <div class="relative">
                                <div class="message-bubble bg-gray-100 dark:bg-gray-700 px-4 py-3 rounded-lg shadow-sm max-w-md">
                                    <img src="https://via.placeholder.com/150" class="rounded-lg max-w-xs" alt="Sent Image"/>
                                    <p class="text-gray-800 dark:text-gray-200 mt-2">Đây là sơ đồ thiết kế nhé!</p>
                                </div>
                                <div class="text-xs text-gray-400 dark:text-gray-500 mt-1">Trang · 14:22</div>
                            </div>
                        </div>
                        <div class="message-container flex justify-end">
                            <div class="relative">
                                <div class="message-bubble bg-gray-800 text-white px-4 py-3 rounded-lg shadow-md max-w-md">
                                    <p>${message}</p>
                                </div>
                                <div class="text-xs text-right text-gray-400 dark:text-gray-500 mt-1">Bạn · 14:23 <i class="fas fa-check-double text-gray-400 ml-1"></i></div>
                            </div>
                        </div>
                    </div>
                    <div class="p-4 border-t bg-white dark:bg-gray-900">
                        <div class="flex items-center gap-2">
                            <button class="action-button text-gray-600 dark:text-gray-300 hover:text-black dark:hover:text-white" title="Gửi ảnh">
                                <i class="fas fa-image"></i>
                            </button>
                            <button class="action-button text-gray-600 dark:text-gray-300 hover:text-black dark:hover:text-white" title="Gửi emoji">
                                <i class="fas fa-smile"></i>
                            </button>
                            <input type="text" class="chat-input flex-grow p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-black dark:focus:ring-gray-300" placeholder="Nhập tin nhắn...">
                            <button class="send-button bg-gray-800 dark:bg-gray-700 text-white px-4 py-3 rounded-lg hover:bg-gray-900 dark:hover:bg-gray-600 transition">Gửi</button>
                        </div>
                    </div>
                </div>
                <div class="chat-info hidden flex-col w-80 bg-white dark:bg-gray-900 border-l border-gray-200 dark:border-gray-700" id="chatInfo">
                    <div class="flex justify-between items-center p-4 border-b border-gray-200 dark:border-gray-700">
                        <h3 class="text-lg font-semibold text-gray-800 dark:text-gray-100">Thông tin nhóm</h3>
                        <button class="close-info-button text-gray-600 dark:text-gray-300 hover:text-black dark:hover:text-white" id="closeInfoBtn">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>
                    <div class="p-4">
                        <div class="mb-4">
                            <h3 class="text-sm font-semibold text-gray-800 dark:text-gray-200">Tìm kiếm trò chuyện</h3>
                            <input type="text" class="w-full p-3 rounded-lg bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-200 focus:outline-none input-field text-base" placeholder="Tìm trong cuộc trò chuyện...">
                        </div>
                        <div class="mb-4">
                            <h3 class="text-sm font-semibold text-gray-800 dark:text-gray-200">Thành viên nhóm</h3>
                            <ul class="space-y-2">
                                <li class="flex items-center">
                                    <img src="https://via.placeholder.com/32" class="w-8 h-8 rounded-full mr-2 avatar" alt="Avatar"/>
                                    <span class="text-gray-800 dark:text-gray-200">Nguyễn Văn A</span>
                                </li>
                                <li class="flex items-center">
                                    <img src="https://via.placeholder.com/32" class="w-8 h-8 rounded-full mr-2 avatar" alt="Avatar"/>
                                    <span class="text-gray-800 dark:text-gray-200">Trần Thị B</span>
                                </li>
                                <li class="flex items-center">
                                    <img src="https://via.placeholder.com/32" class="w-8 h-8 rounded-full mr-2 avatar" alt="Avatar"/>
                                    <span class="text-gray-800 dark:text-gray-200">Bùi Văn Long</span>
                                </li>
                            </ul>
                        </div>
                        <div class="mb-4">
                            <h3 class="text-sm font-semibold text-gray-800 dark:text-gray-200">Ảnh</h3>
                            <div class="flex flex-wrap gap-2">
                                <img src="https://via.placeholder.com/150" class="w-20 h-20 rounded-lg" alt="Shared Image"/>
                                <img src="https://via.placeholder.com/150" class="w-20 h-20 rounded-lg" alt="Shared Image"/>
                            </div>
                        </div>
                        <div class="mb-4">
                            <h3 class="text-sm font-semibold text-gray-800 dark:text-gray-200">File</h3>
                            <ul class="space-y-2">
                                <li class="flex items-center p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg">
                                    <i class="fas fa-file-pdf mr-2 text-red-500"></i> document.pdf
                                </li>
                                <li class="flex items-center p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg">
                                    <i class="fas fa-file-zipper mr-2 text-blue-500"></i> project.zip
                                </li>
                            </ul>
                        </div>
                        <div>
                            <h3 class="text-sm font-semibold text-gray-800 dark:text-gray-200">Tùy chọn nhóm</h3>
                            <button class="option-button w-full text-left p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg" onclick="showRenameGroupOverlay('${group}')">
                                <i class="fas fa-edit mr-2"></i> Đổi tên nhóm
                            </button>
                            <button class="option-button w-full text-left p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg" onclick="showLeaveGroupOverlay('${group}')">
                                <i class="fas fa-sign-out-alt mr-2"></i> Rời nhóm
                            </button>
                        </div>
                    </div>
                </div>
            `;

            // Gắn sự kiện cho nút toggleInfoBtn
            const toggleInfoBtn = document.getElementById('toggleInfoBtn');
            if (toggleInfoBtn) {
                toggleInfoBtn.addEventListener('click', () => {
                    const chatInfo = document.getElementById('chatInfo');
                    if (chatInfo) {
                        chatInfo.classList.toggle('hidden');
                        console.log('Toggled chat-info visibility:', !chatInfo.classList.contains('hidden'));
                    } else {
                        console.error('chatInfo element not found');
                    }
                });
            } else {
                console.error('toggleInfoBtn not found');
            }

            // Gắn sự kiện cho nút closeInfoBtn
            const closeInfoBtn = document.getElementById('closeInfoBtn');
            if (closeInfoBtn) {
                closeInfoBtn.addEventListener('click', () => {
                    const chatInfo = document.getElementById('chatInfo');
                    if (chatInfo) {
                        chatInfo.classList.add('hidden');
                        console.log('Closed chat-info');
                    }
                });
            }
        });
    });
}