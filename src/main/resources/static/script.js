function showMainMenu() {
    const overlayContent = document.getElementById('overlayContent');
    overlayContent.innerHTML = `
            <div class="flex items-center justify-between bg-gray-900 text-white rounded-t-lg p-4">
                <div class="flex items-center">
                    <div class="w-14 h-14 bg-white rounded-full flex items-center justify-center text-black font-bold text-xl">BL</div>
                    <div class="ml-4">
                        <div class="font-semibold text-xl">Bùi Văn Long</div>
                    </div>
                </div>
                <button class="text-white hover:text-gray-300 text-xl" id="closeOverlayBtn"><i class="fas fa-times"></i></button>
            </div>
            <ul class="mt-6 space-y-4" id="mainMenu">
                <li class="flex items-center p-4 hover:bg-gray-100 rounded-lg cursor-pointer transition" data-section="findFriends">
                    <i class="fas fa-heart mr-4 text-black text-lg"></i> <span class="text-lg">Tìm bạn bè</span>
                </li>
                <li class="flex items-center p-4 hover:bg-gray-100 rounded-lg cursor-pointer transition" data-section="friends">
                    <i class="fas fa-user mr-4 text-black text-lg"></i> <span class="text-lg">Bạn bè</span>
                </li>
                <li class="flex items-center p-4 hover:bg-gray-100 rounded-lg cursor-pointer transition" data-section="groups">
                    <i class="fas fa-users mr-4 text-black text-lg"></i> <span class="text-lg">Tất cả nhóm</span>
                </li>
                <li class="flex items-center p-4 hover:bg-gray-100 rounded-lg cursor-pointer transition" data-section="settings">
                    <i class="fas fa-cog mr-4 text-black text-lg"></i> <span class="text-lg">Cài đặt</span>
                </li>
                <li class="flex items-center p-4 hover:bg-gray-100 rounded-lg cursor-pointer transition" data-section="logout">
                    <i class="fas fa-sign-out-alt mr-4 text-black text-lg"></i> <span class="text-lg">Đăng xuất</span>
                </li>
            </ul>
            <div class="text-sm text-gray-500 mt-8 text-center">Phiên bản 1.0.0</div>
        `;

    // Gắn sự kiện menu
    const menuItems = document.querySelectorAll('#mainMenu li');
    menuItems.forEach(item => {
        item.addEventListener('click', () => {
            const section = item.getAttribute('data-section');
            let content = '';
            let title = '';

            switch (section) {
                case 'findFriends':
                    title = 'Tìm bạn bè';
                    content = `
                            <div class="p-4">
                                <input type="text" class="w-full p-3 rounded-lg bg-gray-100 text-gray-800 focus:outline-none input-field text-base" placeholder="Tìm kiếm bạn bè...">
                                <p class="mt-4 text-gray-600">Chưa có kết quả tìm kiếm.</p>
                            </div>
                        `;
                    break;

                case 'friends':
                    title = 'Bạn bè';
                    content = `
                            <div class="p-4">
                                <ul id="friendList" class="space-y-2">
                                    <li>Đang tải danh sách bạn bè...</li>
                                </ul>
                            </div>
                        `;
                    overlayContent.innerHTML = `
                            <div class="flex items-center justify-between bg-gray-900 text-white rounded-t-lg p-4">
                                <div class="flex items-center">
                                    <button class="back-button mr-4 text-xl" id="backBtn"><i class="fas fa-arrow-left"></i></button>
                                    <div class="font-semibold text-xl">${title}</div>
                                </div>
                                <button class="text-white hover:text-gray-300 text-xl" id="closeOverlayBtn"><i class="fas fa-times"></i></button>
                            </div>
                            ${content}
                            <div class="text-sm text-gray-500 mt-8 text-center">Phiên bản 1.0.0</div>
                        `;
                    attachBackListener();
                    attachCloseListener();

                    const token = localStorage.getItem('access_token');
                    const $list = $('#friendList');
                    console.log('Token:', token);

                    if (!token) {
                        $list.html('<li class="text-red-500">Không tìm thấy token người dùng.</li>');
                        return;
                    }

                    const payload = parseJwt(token);
                    console.log('Payload:', payload);
                    const userId = payload?.id;
                    console.log('User ID:', userId);

                    if (!userId) {
                        $list.html('<li class="text-red-500">Không thể xác định người dùng từ token.</li>');
                        return;
                    }

                    $.ajax({
                        url: 'http://localhost:8885/api/friendships/friends',
                        method: 'GET',
                        data: {userId},
                        headers: {'Authorization': `Bearer ${token}`},
                        dataType: 'json',
                        success: function (result) {
                            console.log('API Success Response:', JSON.stringify(result, null, 2));
                            $list.empty();

                            let friends = [];
                            if (result['API Status']?.success && Array.isArray(result.data)) {
                                friends = result.data; // Cấu trúc: { "API Status": { success: true }, data: [] }
                            } else if (Array.isArray(result.data)) {
                                friends = result.data; // Cấu trúc: { data: [] }
                            } else if (Array.isArray(result)) {
                                friends = result; // Cấu trúc: []
                            } else {
                                $list.append(`<li class="text-red-500">Dữ liệu bạn bè không hợp lệ: ${JSON.stringify(result)}</li>`);
                                return;
                            }

                            if (friends.length === 0) {
                                $list.append('<li class="text-gray-500">Không có bạn bè nào.</li>');
                            } else {
                                friends.forEach(name => {
                                    $list.append(`
                                            <li class="flex items-center p-2 hover:bg-gray-100 rounded-lg">${name}</li>
                                        `);
                                });
                            }
                        },
                        error: function (xhr, status, error) {
                            console.error('AJAX Error:', {
                                status: xhr.status,
                                statusText: xhr.statusText,
                                responseText: xhr.responseText,
                                error
                            });
                            $list.html(`<li class="text-red-500">Lỗi khi tải danh sách bạn bè: ${xhr.status} ${xhr.statusText}</li>`);
                        }
                    });
                    break;

                case 'groups':
                    title = 'Tất cả nhóm';
                    content = `
                            <div class="p-4">
                                <ul class="space-y-2">
                                    <li class="flex items-center p-2 hover:bg-gray-100 rounded-lg">Lập trình Java</li>
                                    <li class="flex items-center p-2 hover:bg-gray-100 rounded-lg">Đồ án 2025</li>
                                </ul>
                            </div>
                        `;
                    break;

                case 'settings':
                    title = 'Cài đặt';
                    content = `
                            <div class="p-4">
                                <p class="text-gray-600">Chế độ tối: <input type="checkbox" class="ml-2"></p>
                                <p class="text-gray-600 mt-2">Thông báo: <input type="checkbox" class="ml-2" checked></p>
                            </div>
                        `;
                    break;

                case 'logout':
                    title = 'Đăng xuất';
                    content = `
                            <div class="p-4">
                                <p class="text-gray-600">Bạn có chắc chắn muốn đăng xuất?</p>
                                <button id="confirmLogout" class="mt-4 bg-gray-800 text-white px-4 py-2 rounded-lg hover:bg-gray-900 w-full">Xác nhận</button>
                            </div>
                        `;
                    setTimeout(() => {
                        document.getElementById('confirmLogout')?.addEventListener('click', () => {
                            localStorage.removeItem('access_token');
                            location.reload();
                        });
                    }, 0);
                    break;
            }

            if (section !== 'friends') {
                overlayContent.innerHTML = `
                        <div class="flex items-center justify-between bg-gray-900 text-white rounded-t-lg p-4">
                            <div class="flex items-center">
                                <button class="back-button mr-4 text-xl" id="backBtn"><i class="fas fa-arrow-left"></i></button>
                                <div class="font-semibold text-xl">${title}</div>
                            </div>
                            <button class="text-white hover:text-gray-300 text-xl" id="closeOverlayBtn"><i class="fas fa-times"></i></button>
                        </div>
                        ${content}
                        <div class="text-sm text-gray-500 mt-8 text-center">Phiên bản 1.0.0</div>
                    `;
                attachBackListener();
                attachCloseListener();
            }
        });
    });

    attachCloseListener();
}

function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(c =>
            '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
        ).join(''));
        return JSON.parse(jsonPayload);
    } catch (e) {
        console.error("Token không hợp lệ:", e);
        return null;
    }
}

function attachBackListener() {
    const backBtn = document.getElementById('backBtn');
    if (backBtn) {
        backBtn.removeEventListener('click', showMainMenu);
        backBtn.addEventListener('click', showMainMenu);
    }
}

function attachCloseListener() {
    const closeBtn = document.getElementById('closeOverlayBtn');
    if (closeBtn) {
        closeBtn.removeEventListener('click', closeOverlayHandler);
        closeBtn.addEventListener('click', closeOverlayHandler);
    }
}

function closeOverlayHandler() {
    document.getElementById('menuOverlay').classList.remove('active');
    document.getElementById('overlayContent').innerHTML = '';
}

document.getElementById('toggleSidebarBtn').addEventListener('click', () => {
    const overlay = document.getElementById('menuOverlay');
    overlay.classList.toggle('active');
    showMainMenu();
});

document.getElementById('menuOverlay').addEventListener('click', (e) => {
    if (e.target === e.currentTarget) {
        e.currentTarget.classList.remove('active');
    }
});

document.querySelectorAll('.group-item').forEach(item => {
    item.addEventListener('click', () => {
        document.querySelectorAll('.group-item').forEach(i => i.classList.remove('active'));
        item.classList.add('active');

        const group = item.getAttribute('data-group');
        const message = item.getAttribute('data-message');

        document.getElementById('chatContent').innerHTML = `
                <div class="chat-main flex flex-col h-full">
                    <div class="chat-header flex items-center justify-between">
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
                                <div class="message-options absolute right-2 top-2">
                                    <button class="text-gray-500 hover:text-black"><i class="fas fa-ellipsis-h"></i></button>
                                </div>
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
                                <div class="message-options absolute right-2 top-2">
                                    <button class="text-gray-500 hover:text-black"><i class="fas fa-ellipsis-h"></i></button>
                                </div>
                            </div>
                        </div>
                        <div class="message-container flex justify-end">
                            <div class="relative">
                                <div class="message-bubble bg-gray-800 text-white px-4 py-3 rounded-lg shadow-md max-w-md">
                                    <p>${message}</p>
                                </div>
                                <div class="text-xs text-right text-gray-400 mt-1">Bạn · 14:23 <i class="fas fa-check-double text-black ml-1"></i></div>
                                <div class="message-options absolute right-2 top-2">
                                    <button class="text-white hover:text-gray-200"><i class="fas fa-ellipsis-h"></i></button>
                                </div>
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
                            <button class="send-button px-4 py-3 text-white rounded-lg transition">Gửi</button>
                        </div>
                    </div>
                </div>
                <div class="chat-info flex flex-col" id="chatInfo">
                    <div class="flex justify-between items-center mb-4">
                        <h3 class="text-lg font-semibold text-gray-800">Thông tin nhóm</h3>
                        <button class="close-info-button" id="closeInfoBtn">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>
                    <div class="info-section">
                        <h3>Tìm kiếm trò chuyện</h3>
                        <input type="text" class="w-full p-3 rounded-lg bg-gray-100 text-gray-800 focus:outline-none input-field text-base" placeholder="Tìm trong cuộc trò chuyện...">
                    </div>
                    <div class="info-section">
                        <h3>Thành viên nhóm</h3>
                        <ul class="space-y-2">
                            <li class="member-item">
                                <img src="https://via.placeholder.com/32" class="w-8 h-8 rounded-full mr-2 avatar" alt="Avatar"/>
                                <span>Nguyễn Văn A</span>
                            </li>
                            <li class="member-item">
                                <img src="https://via.placeholder.com/32" class="w-8 h-8 rounded-full mr-2 avatar" alt="Avatar"/>
                                <span>Trần Thị B</span>
                            </li>
                            <li class="member-item">
                                <img src="https://via.placeholder.com/32" class="w-8 h-8 rounded-full mr-2 avatar" alt="Avatar"/>
                                <span>Bùi Văn Long</span>
                            </li>
                        </ul>
                    </div>
                    <div class="info-section">
                        <h3>Ảnh</h3>
                        <div class="flex flex-wrap">
                            <img src="https://via.placeholder.com/150" class="media-item" alt="Shared Image"/>
                            <img src="https://via.placeholder.com/150" class="media-item" alt="Shared Image"/>
                        </div>
                    </div>
                    <div class="info-section">
                        <h3>File</h3>
                        <ul class="space-y-2">
                            <li class="flex items-center p-2 hover:bg-gray-100 rounded-lg">
                                <i class="fas fa-file-pdf mr-2 text-red-500"></i> document.pdf
                            </li>
                            <li class="flex items-center p-2 hover:bg-gray-100 rounded-lg">
                                <i class="fas fa-file-zipper mr-2 text-blue-500"></i> project.zip
                            </li>
                        </ul>
                    </div>
                    <div class="info-section">
                        <h3>Tùy chọn nhóm</h3>
                        <button class="option-button" onclick="showRenameGroupOverlay('${group}')">
                            <i class="fas fa-edit mr-2"></i> Đổi tên nhóm
                        </button>
                        <button class="option-button" onclick="showLeaveGroupOverlay('${group}')">
                            <i class="fas fa-sign-out-alt mr-2"></i> Rời nhóm
                        </button>
                    </div>
                </div>
            `;

        document.getElementById('toggleInfoBtn').addEventListener('click', () => {
            document.getElementById('chatInfo').classList.toggle('active');
        });

        document.getElementById('closeInfoBtn').addEventListener('click', () => {
            document.getElementById('chatInfo').classList.remove('active');
        });
    });
});

function showRenameGroupOverlay(groupName) {
    document.getElementById('menuOverlay').classList.add('active');
    document.getElementById('overlayContent').innerHTML = `
            <div class="flex items-center justify-between bg-gray-900 text-white rounded-t-lg p-4">
                <div class="flex items-center">
                    <button class="back-button mr-4 text-xl" id="backBtn"><i class="fas fa-arrow-left"></i></button>
                    <div class="font-semibold text-xl">Đổi tên nhóm</div>
                </div>
                <button class="text-white hover:text-gray-300 text-xl" id="closeOverlayBtn"><i class="fas fa-times"></i></button>
            </div>
            <div class="p-4">
                <input type="text" class="w-full p-3 rounded-lg bg-gray-100 text-gray-800 focus:outline-none input-field text-base" placeholder="Nhập tên nhóm mới..." value="${groupName}">
                <button class="mt-4 bg-gray-800 text-white px-4 py-2 rounded-lg hover:bg-gray-900 w-full">Lưu</button>
            </div>
            <div class="text-sm text-gray-500 mt-8 text-center">Phiên bản 1.0.0</div>
        `;
    attachBackListener();
    attachCloseListener();
}

function showLeaveGroupOverlay(groupName) {
    document.getElementById('menuOverlay').classList.add('active');
    document.getElementById('overlayContent').innerHTML = `
            <div class="flex items-center justify-between bg-gray-900 text-white rounded-t-lg p-4">
                <div class="flex items-center">
                    <button class="back-button mr-4 text-xl" id="backBtn"><i class="fas fa-arrow-left"></i></button>
                    <div class="font-semibold text-xl">Rời nhóm</div>
                </div>
                <button class="text-white hover:text-gray-300 text-xl" id="closeOverlayBtn"><i class="fas fa-times"></i></button>
            </div>
            <div class="p-4">
                <p class="text-gray-600">Bạn có chắc chắn muốn rời nhóm "${groupName}"?</p>
                <button class="mt-4 bg-gray-800 text-white px-4 py-2 rounded-lg hover:bg-gray-900 w-full">Xác nhận</button>
            </div>
            <div class="text-sm text-gray-500 mt-8 text-center">Phiên bản 1.0.0</div>
        `;
    attachBackListener();
    attachCloseListener();
}
