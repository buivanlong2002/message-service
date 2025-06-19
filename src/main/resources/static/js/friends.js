// js/friends.js
import { parseJwt } from './utils.js';

export function loadFriendsList() {
    const token = localStorage.getItem('access_token');
    const $list = $('#friendList');

    if (!token) {
        $list.html('<li class="text-red-500">Không tìm thấy token người dùng.</li>');
        return;
    }

    const payload = parseJwt(token);
    const userId = payload?.id;

    if (!userId) {
        $list.html('<li class="text-red-500">Không thể xác định người dùng từ token.</li>');
        return;
    }

    $.ajax({
        url: 'http://localhost:8885/api/friendships/friends',
        method: 'GET',
        data: { userId },
        headers: { 'Authorization': `Bearer ${token}` },
        dataType: 'json',
        success: function (result) {
            $list.empty();
            let friends = [];

            if (result['API Status']?.success && Array.isArray(result.data)) {
                friends = result.data;
            } else if (Array.isArray(result.data)) {
                friends = result.data;
            } else if (Array.isArray(result)) {
                friends = result;
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
        error: function (xhr) {
            $list.html(`<li class="text-red-500">Lỗi khi tải danh sách bạn bè: ${xhr.status} ${xhr.statusText}</li>`);
        }
    });
}