// js/group-actions.js
import { attachBackListener, attachCloseListener } from './overlay.js';

export function showRenameGroupOverlay(groupName) {
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

export function showLeaveGroupOverlay(groupName) {
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