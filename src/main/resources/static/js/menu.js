// js/menu.js
import { attachBackListener, attachCloseListener } from './overlay.js';
import { loadFriendsList } from './friends.js';

export function showMainMenu() {
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

    attachMenuEvents();
    attachCloseListener();
}

function attachMenuEvents() {
    const menuItems = document.querySelectorAll('#mainMenu li');
    menuItems.forEach(item => {
        item.addEventListener('click', () => {
            const section = item.getAttribute('data-section');
            handleMenuSelection(section);
        });
    });
}

function handleMenuSelection(section) {
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
            renderSection(title, content);
            loadFriendsList();
            return;
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
            renderSection(title, content);
            document.getElementById('confirmLogout')?.addEventListener('click', () => {
                localStorage.removeItem('access_token');
                location.reload();
            });
            return;
    }

    renderSection(title, content);
}

function renderSection(title, content) {
    const overlayContent = document.getElementById('overlayContent');
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