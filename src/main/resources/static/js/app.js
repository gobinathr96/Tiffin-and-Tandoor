// ---------------------------------------------------------------
// Talks to the Spring Boot REST API at /api/...
// Customers are identified by mobile number (see phone-login below),
// not by a hard-coded id - that hard-coding was the old "Add to cart
// does nothing" bug: if the guessed id didn't exist, every call
// silently failed. Now we always resolve a real id first.
// ---------------------------------------------------------------
const API_BASE = '/api';

let currentUser = null; // { id, name, phone, loyaltyPoints }
let allFoodItems = [];
let activeCategory = 'All';

async function init() {
    const savedPhone = localStorage.getItem('customerPhone');
    if (savedPhone) {
        await loginWithPhone(savedPhone, localStorage.getItem('customerName') || '');
    }
    await loadMenu();
    await loadTodaySpecials();
    await loadTables();
    initScrollReveal();
}

// ---------------- Scroll-reveal animation ----------------
// Fades + rises each major section into view the first time it
// crosses into the viewport. Purely visual; safe no-op if the
// browser lacks IntersectionObserver.
function initScrollReveal() {
    const sections = document.querySelectorAll('.reveal');
    if (!sections.length) return;

    if (!('IntersectionObserver' in window)) {
        sections.forEach(el => el.classList.add('revealed'));
        return;
    }

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('revealed');
                observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.12 });

    sections.forEach(el => observer.observe(el));
}

// ---------------- Phone login ----------------

function showPhoneModal() {
    document.getElementById('phoneOverlay').classList.add('open');
}

function hidePhoneModal() {
    document.getElementById('phoneOverlay').classList.remove('open');
}

async function submitPhone() {
    const phone = document.getElementById('phoneInput').value.trim();
    const name = document.getElementById('nameInput').value.trim();

    if (!/^\d{10}$/.test(phone)) {
        alert('Please enter a valid 10-digit mobile number.');
        return;
    }

    await loginWithPhone(phone, name);
    hidePhoneModal();
}

async function loginWithPhone(phone, name) {
    try {
        const res = await fetch(`${API_BASE}/users/phone-login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ phone, name })
        });
        if (!res.ok) throw new Error('Could not verify that mobile number. Please try again.');

        currentUser = await res.json();
        localStorage.setItem('customerPhone', currentUser.phone);
        localStorage.setItem('customerName', currentUser.name || '');
        updateUserBadge();
        await refreshCart();
    } catch (e) {
        alert(e.message);
        showPhoneModal();
    }
}

function changeNumber() {
    localStorage.removeItem('customerPhone');
    localStorage.removeItem('customerName');
    currentUser = null;
    updateUserBadge();
    showPhoneModal();
}

function updateUserBadge() {
    const badge = document.getElementById('userBadge');
    if (currentUser) {
        badge.innerHTML = `${currentUser.name || 'Guest'} · ${currentUser.phone} · ${currentUser.loyaltyPoints} pts
            <a href="#" onclick="changeNumber(); return false;" style="margin-left:6px;">(switch)</a>`;
    } else {
        badge.textContent = '';
    }
}

// ---------------- Menu ----------------

async function loadMenu() {
    const res = await fetch(`${API_BASE}/food-items`);
    allFoodItems = await res.json();
    renderCategories();
    renderMenu();
}

function renderCategories() {
    const categories = ['All', ...new Set(allFoodItems.map(i => i.category))];
    const container = document.getElementById('categories');
    container.innerHTML = categories.map(cat =>
        `<button class="category-chip ${cat === activeCategory ? 'active' : ''}" onclick="setCategory('${cat}')">${cat}</button>`
    ).join('');
}

function setCategory(cat) {
    activeCategory = cat;
    renderCategories();
    renderMenu();
}

function renderMenu() {
    const items = activeCategory === 'All'
        ? allFoodItems
        : allFoodItems.filter(i => i.category === activeCategory);

    const grid = document.getElementById('menuGrid');
    if (items.length === 0) {
        grid.innerHTML = '<p class="empty-msg">No dishes in this category yet.</p>';
        return;
    }

    grid.innerHTML = items.map(item => `
        <div class="food-card">
            <img class="food-img" src="${item.imageUrl || ''}" alt="${item.name}" loading="lazy"
                 onerror="this.style.display='none'">
            <span class="category-tag">${item.category}</span>
            <h3>${item.name}</h3>
            <p class="desc">${item.description ?? ''}</p>
            <div class="row">
                <span class="price">₹${item.price.toFixed(0)}</span>
                <button class="add-btn" onclick="addToCart(${item.id})">Add</button>
            </div>
        </div>
    `).join('');
}

// ---------------- Today's Special ----------------

async function loadTodaySpecials() {
    try {
        const res = await fetch(`${API_BASE}/food-items/today-special`);
        if (!res.ok) throw new Error('Could not load today\'s specials.');
        const items = await res.json();
        renderTodaySpecials(items);
    } catch (e) {
        console.error(e);
        document.getElementById('specialGrid').innerHTML = '<p class="empty-msg">No specials right now — check back soon.</p>';
    }
}

function renderTodaySpecials(items) {
    const grid = document.getElementById('specialGrid');
    if (!items || items.length === 0) {
        grid.innerHTML = '<p class="empty-msg">No specials today — check the full menu below.</p>';
        return;
    }
    grid.innerHTML = items.map(item => `
        <div class="food-card">
            <span class="special-flag">★ Special</span>
            <img class="food-img" src="${item.imageUrl || ''}" alt="${item.name}" loading="lazy"
                 onerror="this.style.display='none'">
            <span class="category-tag">${item.category}</span>
            <h3>${item.name}</h3>
            <p class="desc">${item.description ?? ''}</p>
            <div class="row">
                <span class="price">₹${item.price.toFixed(0)}</span>
                <button class="add-btn" onclick="addToCart(${item.id})">Add</button>
            </div>
        </div>
    `).join('');
}

// ---------------- Cart ----------------

async function addToCart(foodItemId) {
    if (!currentUser) {
        showPhoneModal();
        return;
    }
    try {
        const res = await fetch(`${API_BASE}/cart/${currentUser.id}/items`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ foodItemId, quantity: 1 })
        });
        if (!res.ok) throw new Error('Could not add that item to your cart. Please try again.');

        await refreshCart();
        document.getElementById('cartDrawer').classList.add('open');
    } catch (e) {
        alert(e.message);
    }
}

async function refreshCart() {
    if (!currentUser) return;

    try {
        const res = await fetch(`${API_BASE}/cart/${currentUser.id}`);
        if (!res.ok) throw new Error('Could not load your cart.');
        const cart = await res.json();

        const cartCountEl = document.getElementById('cartCount');
        if (cartCountEl.textContent !== String(cart.items.length)) {
            cartCountEl.textContent = cart.items.length;
            cartCountEl.classList.remove('bump');
            // restart the animation even if it's already mid-way
            void cartCountEl.offsetWidth;
            cartCountEl.classList.add('bump');
        }

        const itemsContainer = document.getElementById('cartItems');
        if (cart.items.length === 0) {
            itemsContainer.innerHTML = '<p class="empty-msg">Your cart is empty.</p>';
        } else {
            itemsContainer.innerHTML = cart.items.map(ci => `
                <div class="cart-item">
                    <span>${ci.foodItem.name} × ${ci.quantity}</span>
                    <span>₹${(ci.foodItem.price * ci.quantity).toFixed(0)}</span>
                </div>
            `).join('');
        }

        const total = cart.items.reduce((sum, ci) => sum + ci.foodItem.price * ci.quantity, 0);
        document.getElementById('cartTotal').textContent = `₹${total.toFixed(0)}`;
    } catch (e) {
        console.error(e);
    }
}

function toggleCart() {
    if (!currentUser) {
        showPhoneModal();
        return;
    }
    document.getElementById('cartDrawer').classList.toggle('open');
}

// ---------------- Table Management & Booking ----------------

let pendingTableId = null;

async function loadTables() {
    try {
        const res = await fetch(`${API_BASE}/tables`);
        if (!res.ok) throw new Error('Could not load tables.');
        const tables = await res.json();
        renderTables(tables);
    } catch (e) {
        console.error(e);
        document.getElementById('tableGrid').innerHTML = '<p class="empty-msg">Could not load tables right now.</p>';
    }
}

function tableTypeLabel(type) {
    if (type === 'FOUR_SEATER') return '4-Seater Table';
    if (type === 'FAMILY') return 'Family Table';
    if (type === 'COUPLE') return 'Couple Table';
    return type;
}

function renderTables(tables) {
    const grid = document.getElementById('tableGrid');
    if (!tables || tables.length === 0) {
        grid.innerHTML = '<p class="empty-msg">No tables configured yet.</p>';
        return;
    }
    grid.innerHTML = tables.map(t => {
        const isBooked = t.status === 'BOOKED';
        return `
        <div class="table-card ${isBooked ? 'booked' : ''}">
            <span class="table-status-pill ${isBooked ? 'booked-pill' : 'available'}">${isBooked ? 'Booked' : 'Available'}</span>
            <div class="table-number">${t.tableNumber}</div>
            <span class="table-type">${tableTypeLabel(t.type)}</span>
            <span class="table-capacity">Seats ${t.capacity}</span>
            ${isBooked
                ? `<button class="release-btn" onclick="releaseTable(${t.id})">Release table</button>`
                : `<button class="book-btn" onclick="openTableBooking(${t.id}, '${t.tableNumber}')">Book table</button>`}
        </div>`;
    }).join('');
}

function openTableBooking(tableId, tableNumber) {
    pendingTableId = tableId;
    document.getElementById('tableBookingTitle').textContent = `Book table ${tableNumber}`;
    if (currentUser) {
        document.getElementById('tableBookingName').value = currentUser.name || '';
        document.getElementById('tableBookingPhone').value = currentUser.phone || '';
    }
    document.getElementById('tableBookingOverlay').classList.add('open');
}

function closeTableBooking() {
    pendingTableId = null;
    document.getElementById('tableBookingOverlay').classList.remove('open');
}

async function confirmTableBooking() {
    const name = document.getElementById('tableBookingName').value.trim();
    const phone = document.getElementById('tableBookingPhone').value.trim();
    if (!name || !/^\d{10}$/.test(phone)) {
        alert('Please enter your name and a valid 10-digit mobile number.');
        return;
    }
    try {
        const res = await fetch(`${API_BASE}/tables/${pendingTableId}/book`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, phone })
        });
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.error || 'Could not book that table.');
        }
        const booked = await res.json();
        localStorage.setItem('bookedTableNumber', booked.tableNumber);
        closeTableBooking();
        await loadTables();
        alert(`Table ${booked.tableNumber} booked! It'll be applied to your next order's bill.`);
    } catch (e) {
        alert(e.message);
    }
}

async function releaseTable(tableId) {
    if (!confirm('Release this table so someone else can book it?')) return;
    try {
        const res = await fetch(`${API_BASE}/tables/${tableId}/release`, { method: 'POST' });
        if (!res.ok) throw new Error('Could not release that table.');
        const released = await res.json();
        if (localStorage.getItem('bookedTableNumber') === released.tableNumber) {
            localStorage.removeItem('bookedTableNumber');
        }
        await loadTables();
    } catch (e) {
        alert(e.message);
    }
}

// ---------------- Checkout / bill ----------------

async function checkout() {
    if (!currentUser) {
        showPhoneModal();
        return;
    }

    const address = prompt('Delivery address:', '221B Baker Street');
    if (!address) return;

    const savedTable = localStorage.getItem('bookedTableNumber') || '';
    const tableNumber = prompt('Dine-in table number (leave blank for home delivery):', savedTable);

    try {
        const res = await fetch(`${API_BASE}/orders`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ userId: currentUser.id, deliveryAddress: address, tableNumber: tableNumber || null })
        });

        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.error || 'Could not place order.');
        }

        const data = await res.json(); // { order, bill, loyaltyPointsBalance }
        currentUser.loyaltyPoints = data.loyaltyPointsBalance;
        updateUserBadge();

        showBill(data.order.id, data.bill);
        await refreshCart();
        toggleCart();
    } catch (e) {
        alert(e.message);
    }
}

function showBill(orderId, billText) {
    document.getElementById('billText').textContent = billText;

    const blob = new Blob([billText], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const link = document.getElementById('downloadBillLink');
    link.href = url;
    link.setAttribute('download', `invoice-${orderId}.txt`);

    document.getElementById('billOverlay').classList.add('open');
}

function closeBill() {
    document.getElementById('billOverlay').classList.remove('open');
}

init();
