(function initCollectionsPage() {
    const pageBody = document.getElementById('page-body');
    pageBody.appendChild(document.getElementById('collections-template').content.cloneNode(true));

    const isCitizen = CURRENT_USER.role === 'CITIZEN';
    const modal = document.getElementById('collection-modal');
    const form = document.getElementById('collection-form');
    const modalTitle = document.getElementById('modal-title');

    let allCollections = [];
    let citizens = [];
    let workers = [];
    let vehicles = [];

    if (isCitizen) {
        document.getElementById('add-btn').style.display = 'none';
    }

    init();

    document.getElementById('add-btn').addEventListener('click', () => openModal());
    document.getElementById('cancel-btn').addEventListener('click', closeModal);
    document.getElementById('area-filter').addEventListener('input', debounce(renderFiltered, 250));
    document.getElementById('type-filter').addEventListener('change', renderFiltered);
    document.getElementById('status-filter').addEventListener('change', renderFiltered);
    document.getElementById('date-filter').addEventListener('change', renderFiltered);

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const dto = {
            citizenId: Number(document.getElementById('citizenId').value),
            workerId: document.getElementById('workerId').value ? Number(document.getElementById('workerId').value) : null,
            vehicleId: document.getElementById('vehicleId').value ? Number(document.getElementById('vehicleId').value) : null,
            collectionDate: document.getElementById('collectionDate').value,
            area: document.getElementById('area').value.trim(),
            wasteType: document.getElementById('wasteType').value,
            quantity: Number(document.getElementById('quantity').value),
            status: document.getElementById('status').value
        };
        const id = document.getElementById('collectionId').value;

        try {
            if (id) {
                await apiPut(`/collections/${id}`, dto);
                showToast('Collection updated successfully');
            } else {
                await apiPost('/collections', dto);
                showToast('Collection added successfully');
            }
            closeModal();
            loadCollections();
        } catch (err) {
            handleApiError(err);
        }
    });

    async function init() {
        try {
            const [c, w, v] = await Promise.all([
                apiGet('/citizens'),
                apiGet('/workers'),
                apiGet('/vehicles')
            ]);
            citizens = c; workers = w; vehicles = v;
            populateSelect('citizenId', citizens, 'citizenId', 'name');
            populateSelect('workerId', workers, 'workerId', 'name', true);
            populateSelect('vehicleId', vehicles, 'vehicleId', 'vehicleNumber', true);
            await loadCollections();
        } catch (err) {
            handleApiError(err);
        }
    }

    function populateSelect(elementId, items, valueKey, labelKey, keepFirst) {
        const select = document.getElementById(elementId);
        const preserved = keepFirst ? select.innerHTML : '';
        select.innerHTML = preserved + items.map(item =>
            `<option value="${item[valueKey]}">${escapeHtml(item[labelKey])}</option>`).join('');
    }

    async function loadCollections() {
        try {
            const path = isCitizen ? `/collections?citizenId=${CURRENT_USER.citizenId}` : '/collections';
            allCollections = await apiGet(path);
            renderFiltered();
        } catch (err) {
            handleApiError(err);
        }
    }

    function renderFiltered() {
        const area = document.getElementById('area-filter').value.trim().toLowerCase();
        const type = document.getElementById('type-filter').value;
        const status = document.getElementById('status-filter').value;
        const date = document.getElementById('date-filter').value;

        let filtered = allCollections;
        if (area) filtered = filtered.filter(c => c.area.toLowerCase().includes(area));
        if (type) filtered = filtered.filter(c => c.wasteType === type);
        if (status) filtered = filtered.filter(c => c.status === status);
        if (date) filtered = filtered.filter(c => c.collectionDate === date);

        renderTable(filtered);
    }

    function renderTable(collections) {
        const tbody = document.getElementById('collections-body');
        if (collections.length === 0) {
            tbody.innerHTML = `<tr class="empty-row"><td colspan="10">No collection records found.</td></tr>`;
            return;
        }
        tbody.innerHTML = collections
            .sort((a, b) => new Date(b.collectionDate) - new Date(a.collectionDate))
            .map(c => `
            <tr>
                <td>#${c.collectionId}</td>
                <td>${escapeHtml(c.citizenName)}</td>
                <td>${escapeHtml(c.area)}</td>
                <td>${c.wasteType}</td>
                <td>${formatNumber(c.quantity)}</td>
                <td>${escapeHtml(c.workerName || '-')}</td>
                <td>${escapeHtml(c.vehicleNumber || '-')}</td>
                <td>${formatDate(c.collectionDate)}</td>
                <td>${statusBadge(c.status)}</td>
                <td class="actions-cell">
                    ${isCitizen ? '' : `
                        <button class="btn btn-secondary btn-sm" onclick="editCollection(${c.collectionId})">Edit</button>
                        <button class="btn btn-danger btn-sm" onclick="deleteCollection(${c.collectionId})">Delete</button>
                    `}
                </td>
            </tr>
        `).join('');
    }

    window.editCollection = function (id) {
        const collection = allCollections.find(c => c.collectionId === id);
        if (collection) openModal(collection);
    };

    window.deleteCollection = async function (id) {
        if (!confirm('Delete this collection record?')) return;
        try {
            await apiDelete(`/collections/${id}`);
            showToast('Collection deleted');
            loadCollections();
        } catch (err) {
            handleApiError(err);
        }
    };

    function openModal(collection) {
        form.reset();
        if (collection) {
            modalTitle.textContent = 'Edit Collection';
            document.getElementById('collectionId').value = collection.collectionId;
            document.getElementById('citizenId').value = collection.citizenId;
            document.getElementById('workerId').value = collection.workerId || '';
            document.getElementById('vehicleId').value = collection.vehicleId || '';
            document.getElementById('collectionDate').value = collection.collectionDate;
            document.getElementById('area').value = collection.area;
            document.getElementById('wasteType').value = collection.wasteType;
            document.getElementById('quantity').value = collection.quantity;
            document.getElementById('status').value = collection.status;
        } else {
            modalTitle.textContent = 'Add Collection';
            document.getElementById('collectionId').value = '';
            document.getElementById('collectionDate').value = new Date().toISOString().split('T')[0];
        }
        modal.classList.add('open');
    }

    function closeModal() {
        modal.classList.remove('open');
    }

    function debounce(fn, delay) {
        let timer;
        return (...args) => {
            clearTimeout(timer);
            timer = setTimeout(() => fn(...args), delay);
        };
    }
})();
