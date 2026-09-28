/* ===================== COMMON APP UTILITIES ===================== */
const API_BASE = '/api';

/* ---------- Auth helpers (simple localStorage-based session) ---------- */
function getCurrentUser() {
    const raw = localStorage.getItem('wm_user');
    return raw ? JSON.parse(raw) : null;
}

function requireAuth() {
    const user = getCurrentUser();
    if (!user) {
        window.location.href = 'login.html';
        return null;
    }
    return user;
}

function logout() {
    localStorage.removeItem('wm_user');
    window.location.href = 'login.html';
}

/* ---------- Generic fetch wrapper ---------- */
async function apiRequest(path, options = {}) {
    const opts = Object.assign({
        headers: { 'Content-Type': 'application/json' }
    }, options);

    let response;
    try {
        response = await fetch(API_BASE + path, opts);
    } catch (err) {
        throw new Error('Could not reach the server. Please check the backend is running.');
    }

    if (response.status === 204) {
        return null;
    }

    let data = null;
    try {
        data = await response.json();
    } catch (e) {
        data = null;
    }

    if (!response.ok) {
        const message = (data && data.message) ? data.message : `Request failed (${response.status})`;
        const err = new Error(message);
        err.fieldErrors = data ? data.fieldErrors : null;
        throw err;
    }

    return data;
}

function apiGet(path) { return apiRequest(path, { method: 'GET' }); }
function apiPost(path, body) { return apiRequest(path, { method: 'POST', body: JSON.stringify(body) }); }
function apiPut(path, body) { return apiRequest(path, { method: 'PUT', body: JSON.stringify(body) }); }
function apiPatch(path, body) { return apiRequest(path, { method: 'PATCH', body: JSON.stringify(body) }); }
function apiDelete(path) { return apiRequest(path, { method: 'DELETE' }); }

/* ---------- Toast notifications ---------- */
function showToast(message, type = 'success') {
    let toast = document.getElementById('global-toast');
    if (!toast) {
        toast = document.createElement('div');
        toast.id = 'global-toast';
        toast.className = 'toast';
        document.body.appendChild(toast);
    }
    toast.textContent = message;
    toast.className = `toast show ${type}`;
    clearTimeout(toast._timer);
    toast._timer = setTimeout(() => {
        toast.classList.remove('show');
    }, 3200);
}

/* ---------- Sidebar / topbar shell ---------- */
const NAV_ITEMS = [
    { page: 'dashboard', label: 'Dashboard', icon: '📊', href: 'dashboard.html', roles: ['ADMIN', 'CITIZEN'] },
    { page: 'citizens', label: 'Citizens', icon: '👤', href: 'citizens.html', roles: ['ADMIN'] },
    { page: 'workers', label: 'Workers', icon: '🧹', href: 'workers.html', roles: ['ADMIN'] },
    { page: 'vehicles', label: 'Vehicles', icon: '🚛', href: 'vehicles.html', roles: ['ADMIN'] },
    { page: 'collections', label: 'Waste Collections', icon: '🗑️', href: 'collections.html', roles: ['ADMIN', 'CITIZEN'] },
    { page: 'segregation', label: 'Segregation', icon: '♻️', href: 'segregation.html', roles: ['ADMIN'] },
    { page: 'complaints', label: 'Complaints', icon: '📝', href: 'complaints.html', roles: ['ADMIN', 'CITIZEN'] },
    { page: 'reports', label: 'Reports', icon: '📈', href: 'reports.html', roles: ['ADMIN'] }
];

function renderShell(activePage, pageTitle) {
    const user = requireAuth();
    if (!user) return null;

    const navHtml = NAV_ITEMS
        .filter(item => item.roles.includes(user.role))
        .map(item => `<a href="${item.href}" class="${item.page === activePage ? 'active' : ''}">
                <span>${item.icon}</span><span>${item.label}</span>
             </a>`)
        .join('');

    document.body.insertAdjacentHTML('afterbegin', `
        <div class="app-shell">
            <aside class="sidebar" id="sidebar">
                <div class="brand">
                    🌿 SMART WASTE
                    <span>Municipal Tracker</span>
                </div>
                <nav>
                    ${navHtml}
                    <a href="#" id="logout-link"><span>🚪</span><span>Logout</span></a>
                </nav>
            </aside>
            <div class="main-content">
                <div class="topbar">
                    <div style="display:flex;align-items:center;gap:12px;">
                        <button class="menu-toggle" id="menu-toggle">☰</button>
                        <h2>${pageTitle}</h2>
                    </div>
                    <div class="user-info">
                        <span class="user-badge">${user.role}</span>
                        <span>${user.username}</span>
                    </div>
                </div>
                <div class="page-body" id="page-body"></div>
            </div>
        </div>
    `);

    document.getElementById('logout-link').addEventListener('click', (e) => {
        e.preventDefault();
        logout();
    });

    const menuToggle = document.getElementById('menu-toggle');
    if (menuToggle) {
        menuToggle.addEventListener('click', () => {
            document.getElementById('sidebar').classList.toggle('open');
        });
    }

    return user;
}

/* ---------- Formatting helpers ---------- */
function formatDate(dateStr) {
    if (!dateStr) return '-';
    const d = new Date(dateStr);
    if (isNaN(d.getTime())) return dateStr;
    return d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
}

function formatNumber(n) {
    if (n === null || n === undefined) return '0';
    return Number(n).toLocaleString('en-IN', { maximumFractionDigits: 2 });
}

function statusBadge(status) {
    const map = {
        COLLECTED: 'badge-green', PROPER: 'badge-green', RESOLVED: 'badge-green', ACTIVE: 'badge-green',
        AVAILABLE: 'badge-green',
        PENDING: 'badge-yellow', PARTIAL: 'badge-yellow', ASSIGNED: 'badge-yellow', ON_LEAVE: 'badge-yellow',
        ON_ROUTE: 'badge-blue', IN_PROGRESS: 'badge-blue',
        MISSED: 'badge-red', IMPROPER: 'badge-red', REJECTED: 'badge-red', INACTIVE: 'badge-red',
        MAINTENANCE: 'badge-gray'
    };
    const cls = map[status] || 'badge-gray';
    return `<span class="badge ${cls}">${(status || '').replace('_', ' ')}</span>`;
}

function escapeHtml(str) {
    if (str === null || str === undefined) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}

function handleApiError(err) {
    console.error(err);
    showToast(err.message || 'Something went wrong', 'error');
}
