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
                    <div class="chat-header flex items-center justify-between p-4 bg-white border-b">
                        <div class="flex items-center">
                            <img src="https://via.placeholder.com/40" class="w-10 h-10 rounded-full mr-3 avatar" alt="Group Avatar">
                            <div>
                                <h2 class="text-xl font-bold text-gray-800">${group}</h2>
                                <p class="text-sm text-gray-500">3 thành viên, 2 online</p>
                            </div>
                        </div>
                        <div class="flex items-center space-x-4">
                            <button class="action-button text-gray-600 hover:text-black" title="Gọi thoại">
                                <i class="fas fa-phone-alt text-lg"></i>
                            </button>
                            <button class="action-button text-gray-600 hover:text-black" title="Gọi video">
                                <i class="fas fa-video text-lg"></i>
                            </button>
                            <button class="action-button text-gray-600 hover:text-black" title="Tùy chọn" id="toggleInfoBtn">
                                <i class="fas fa-ellipsis-v text-lg"></i>
                            </button>
                        </div>
                    </div>
                    <div class="flex-grow overflow-y-auto space-y-4 p-6">
                        <div class="message-container flex items-start space-x-3">
                            <img src="https://via.placeholder.com/32" class="w-8 h-8 rounded-full avatar" alt="Avatar"/>
                            <div class="relative">
                                <div class="message-bubble bg-gray-100 px-4 py-3 rounded-lg shadow-sm max-w-md">
                                    <p class="text-gray-800">Chào cả nhóm, mai Sion nhắn tối nay nhé!</p>
                                </div>
                                <div class="text-xs text-gray-400 mt-1">Sion · 14:21</div>
                            </div>
                        </div>
                        <div class="message-container flex items-start space-x-3">
                            <img src="https://via.placeholder.com/32" class="w-8 h-8 rounded-full avatar" alt="Avatar"/>
                            <div class="relative">
                                <div class="message-bubble bg-gray-100 px-4 py-3 rounded-lg shadow-sm max-w-md">
                                    <img src="https://via.placeholder.com/150" class="rounded-lg max-w-xs" alt="Sent Image"/>
                                    <p class="text-gray-800 mt-2">Đây là sơ đồ thiết kế nhé!</p>
                                </div>
                                <div class="text-xs text-gray-400 mt-1">Trang · 14:22</div>
                            </div>
                        </div>
                        <div class="message-container flex justify-end">
                            <div class="relative">
                                <div class="message-bubble bg-gray-800 text-white px-4 py-3 rounded-lg shadow-md max-w-md">
                                    <p>${message}</p>
                                </div>
                                <div class="text-xs text-right text-gray-400 mt-1">Bạn · 14:23 <i class="fas fa-check-double text-black ml-1"></i></div>
                            </div>
                        </div>
                    </div>
                    <div class="p-4 border-t bg-white">
                        <div class="flex items-center gap-2">
                            <button class="action-button text-gray-600 hover:text-black" title="Gửi ảnh">
                                <i class="fas fa-image"></i>
                            </button>
                            <button class="action-button text-gray-600 hover:text-black" title="Gửi emoji">
                                <i class="fas fa-smile"></i>
                            </button>
                            <input type="text" class="chat-input flex-grow p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-black" placeholder="Nhập tin nhắn...">
                            <button class="send-button bg-gray-800 text-white px-4 py-3 rounded-lg hover:bg-gray-900 transition">Gửi</button>
                        </div>
                    </div>
                </div>
                <div class="chat-info hidden flex-col w-80 bg-white border-l" id="chatInfo">
                    <div class="flex justify-between items-center p-4 border-b">
                        <h3 class="text-lg font-semibold text-gray-800">Thông tin nhóm</h3>
                        <button class="close-info-button text-gray-600 hover:text-black" id="closeInfoBtn">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>
                    <div class="p-4">
                        <div class="mb-4">
                            <h3 class="text-sm font-semibold">Tìm kiếm trò chuyện</h3>
                            <input type="text" class="w-full p-3 rounded-lg bg-gray-100 text-gray-800 focus:outline-none input-field text-base" placeholder="Tìm trong cuộc trò chuyện...">
                        </div>
                        <div class="mb-4">
                            <h3 class="text-sm font-semibold">Thành viên nhóm</h3>
                            <ul class="space-y-2">
                                <li class="flex items-center">
                                    <img src="https://via.placeholder.com/32" class="w-8 h-8 rounded-full mr-2 avatar" alt="Avatar"/>
                                    <span>Nguyễn Văn A</span>
                                </li>
                                <li class="flex items-center">
                                    <img src="https://via.placeholder.com/32" class="w-8 h-8 rounded-full mr-2 avatar" alt="Avatar"/>
                                    <span>Trần Thị B</span>
                                </li>
                                <li class="flex items-center">
                                    <img src="https://via.placeholder.com/32" class="w-8 h-8 rounded-full mr-2 avatar" alt="Avatar"/>
                                    <span>Bùi Văn Long</span>
                                </li>
                            </ul>
                        </div>
                        <div class="mb-4">
                            <h3 class="text-sm font-semibold">Ảnh</h3>
                            <div class="flex flex-wrap gap-2">
                                <img src="https://via.placeholder.com/150" class="w-20 h-20 rounded-lg" alt="Shared Image"/>
                                <img src="https://via.placeholder.com/150" class="w-20 h-20 rounded-lg" alt="Shared Image"/>
                            </div>
                        </div>
                        <div class="mb-4">
                            <h3 class="text-sm font-semibold">File</h3>
                            <ul class="space-y-2">
                                <li class="flex items-center p-2 hover:bg-gray-100 rounded-lg">
                                    <i class="fas fa-file-pdf mr-2 text-red-500"></i> document.pdf
                                </li>
                                <li class="flex items-center p-2 hover:bg-gray-100 rounded-lg">
                                    <i class="fas fa-file-zipper mr-2 text-blue-500"></i> project.zip
                                </li>
                            </ul>
                        </div>
                        <div>
                            <h3 class="text-sm font-semibold">Tùy chọn nhóm</h3>
                            <button class="option-button w-full text-left p-2 hover:bg-gray-100 rounded-lg" onclick="showRenameGroupOverlay('${group}')">
                                <i class="fas fa-edit mr-2"></i> Đổi tên nhóm
                            </button>
                            <button class="option-button w-full text-left p-2 hover:bg-gray-100 rounded-lg" onclick="showLeaveGroupOverlay('${group}')">
                                <i class="fas fa-sign-out-alt mr-2"></i> Rời nhóm
                            </button>
                        </div>
                    </div>
                </div>
            `;

            document.getElementById('toggleInfoBtn').addEventListener('click', () => {
                document.getElementById('chatInfo').classList.toggle('hidden');
            });

            document.getElementById('closeInfoBtn').addEventListener('click', () => {
                document.getElementById('chatInfo').classList.add('hidden');
            });
        });
    });
}