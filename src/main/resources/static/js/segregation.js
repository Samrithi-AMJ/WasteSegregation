(function initSegregationPage() {
    const pageBody = document.getElementById('page-body');
    pageBody.appendChild(document.getElementById('segregation-template').content.cloneNode(true));

    const modal = document.getElementById('segregation-modal');
    const form = document.getElementById('segregation-form');
    const modalTitle = document.getElementById('modal-title');

    let allSegregations = [];
    let allCollections = [];

    init();

    document.getElementById('add-btn').addEventListener('click', () => openModal());
    document.getElementById('cancel-btn').addEventListener('click', closeModal);
    document.getElementById('status-filter').addEventListener('change', renderFiltered);

    ['wetQuantity', 'dryQuantity', 'plasticQuantity', 'paperQuantity', 'otherQuantity'].forEach(id => {
        document.getElementById(id).addEventListener('input', updateTotalDisplay);
    });

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const dto = {
            collectionId: Number(document.getElementById('collectionId').value),
            wetQuantity: Number(document.getElementById('wetQuantity').value) || 0,
            dryQuantity: Number(document.getElementById('dryQuantity').value) || 0,
            plasticQuantity: Number(document.getElementById('plasticQuantity').value) || 0,
            paperQuantity: Number(document.getElementById('paperQuantity').value) || 0,
            otherQuantity: Number(document.getElementById('otherQuantity').value) || 0,
            segregationStatus: document.getElementById('segregationStatus').value || null
        };
        const id = document.getElementById('segregationId').value;

        try {
            if (id) {
                await apiPut(`/segregations/${id}`, dto);
                showToast('Segregation record updated');
            } else {
                await apiPost('/segregations', dto);
                showToast('Segregation record added');
            }
            closeModal();
            loadSegregations();
        } catch (err) {
            handleApiError(err);
        }
    });

    async function init() {
        try {
            allCollections = await apiGet('/collections');
            const select = document.getElementById('collectionId');
            select.innerHTML = allCollections.map(c =>
                `<option value="${c.collectionId}">#${c.collectionId} - ${escapeHtml(c.citizenName)} (${escapeHtml(c.area)}, ${c.wasteType}, ${c.quantity}kg)</option>`
            ).join('');
            await loadSegregations();
        } catch (err) {
            handleApiError(err);
        }
    }

    async function loadSegregations() {
        try {
            allSegregations = await apiGet('/segregations');
            renderFiltered();
        } catch (err) {
            handleApiError(err);
        }
    }

    function renderFiltered() {
        const filter = document.getElementById('status-filter').value;
        const filtered = filter ? allSegregations.filter(s => s.segregationStatus === filter) : allSegregations;
        renderTable(filtered);
    }

    function findCollection(id) {
        return allCollections.find(c => c.collectionId === id);
    }

    function renderTable(records) {
        const tbody = document.getElementById('segregation-body');
        if (records.length === 0) {
            tbody.innerHTML = `<tr class="empty-row"><td colspan="11">No segregation records found.</td></tr>`;
            return;
        }
        tbody.innerHTML = records.map(s => {
            const collection = findCollection(s.collectionId);
            return `
            <tr>
                <td>#${s.segregationId}</td>
                <td>#${s.collectionId}</td>
                <td>${collection ? escapeHtml(collection.area) : '-'}</td>
                <td>${formatNumber(s.wetQuantity)}</td>
                <td>${formatNumber(s.dryQuantity)}</td>
                <td>${formatNumber(s.plasticQuantity)}</td>
                <td>${formatNumber(s.paperQuantity)}</td>
                <td>${formatNumber(s.otherQuantity)}</td>
                <td><strong>${formatNumber(s.totalQuantity)}</strong></td>
                <td>${statusBadge(s.segregationStatus)}</td>
                <td class="actions-cell">
                    <button class="btn btn-secondary btn-sm" onclick="editSegregation(${s.segregationId})">Edit</button>
                    <button class="btn btn-danger btn-sm" onclick="deleteSegregation(${s.segregationId})">Delete</button>
                </td>
            </tr>
        `;
        }).join('');
    }

    window.editSegregation = function (id) {
        const record = allSegregations.find(s => s.segregationId === id);
        if (record) openModal(record);
    };

    window.deleteSegregation = async function (id) {
        if (!confirm('Delete this segregation record?')) return;
        try {
            await apiDelete(`/segregations/${id}`);
            showToast('Segregation record deleted');
            loadSegregations();
        } catch (err) {
            handleApiError(err);
        }
    };

    function openModal(record) {
        form.reset();
        if (record) {
            modalTitle.textContent = 'Edit Segregation';
            document.getElementById('segregationId').value = record.segregationId;
            document.getElementById('collectionId').value = record.collectionId;
            document.getElementById('wetQuantity').value = record.wetQuantity;
            document.getElementById('dryQuantity').value = record.dryQuantity;
            document.getElementById('plasticQuantity').value = record.plasticQuantity;
            document.getElementById('paperQuantity').value = record.paperQuantity;
            document.getElementById('otherQuantity').value = record.otherQuantity;
            document.getElementById('segregationStatus').value = record.segregationStatus || '';
        } else {
            modalTitle.textContent = 'Add Segregation';
            document.getElementById('segregationId').value = '';
        }
        updateTotalDisplay();
        modal.classList.add('open');
    }

    function closeModal() {
        modal.classList.remove('open');
    }

    function updateTotalDisplay() {
        const total = ['wetQuantity', 'dryQuantity', 'plasticQuantity', 'paperQuantity', 'otherQuantity']
            .reduce((sum, id) => sum + (Number(document.getElementById(id).value) || 0), 0);
        document.getElementById('totalDisplay').value = `${total.toFixed(2)} kg`;
    }
})();
