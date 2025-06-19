// js/main.js
import { initOverlay } from './overlay.js';
import { initChat } from './chat.js';

function initApp() {
    initOverlay();
    initChat();
    initThemeToggle();
}

function initThemeToggle() {
    const toggleThemeBtn = document.getElementById('toggleThemeBtn');
    if (toggleThemeBtn) {
        toggleThemeBtn.addEventListener('click', () => {
            document.body.classList.toggle('dark-mode');
            const icon = toggleThemeBtn.querySelector('i');
            if (document.body.classList.contains('dark-mode')) {
                icon.classList.replace('fa-moon', 'fa-sun');
            } else {
                icon.classList.replace('fa-sun', 'fa-moon');
            }
        });
    }
}

document.addEventListener('DOMContentLoaded', initApp);