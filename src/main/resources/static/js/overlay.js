// js/overlay.js
import { showMainMenu } from './menu.js';

export function attachBackListener() {
    const backBtn = document.getElementById('backBtn');
    if (backBtn) {
        backBtn.removeEventListener('click', showMainMenu);
        backBtn.addEventListener('click', showMainMenu);
    }
}

export function attachCloseListener() {
    const closeBtn = document.getElementById('closeOverlayBtn');
    if (closeBtn) {
        closeBtn.removeEventListener('click', closeOverlayHandler);
        closeBtn.addEventListener('click', closeOverlayHandler);
    }
}

export function closeOverlayHandler() {
    document.getElementById('menuOverlay').classList.remove('active');
    document.getElementById('overlayContent').innerHTML = '';
}

export function initOverlay() {
    const toggleSidebarBtn = document.getElementById('toggleSidebarBtn');
    if (toggleSidebarBtn) {
        toggleSidebarBtn.addEventListener('click', () => {
            const overlay = document.getElementById('menuOverlay');
            overlay.classList.toggle('active');
            showMainMenu();
        });
    }

    const menuOverlay = document.getElementById('menuOverlay');
    if (menuOverlay) {
        menuOverlay.addEventListener('click', (e) => {
            if (e.target === e.currentTarget) {
                e.currentTarget.classList.remove('active');
            }
        });
    }
}