(function initWorkersPage() {
    const pageBody = document.getElementById('page-body');
    pageBody.appendChild(document.getElementById('workers-template').content.cloneNode(true));

    const modal = document.getElementById('worker-modal');
    const form = document.getElementById('worker-form');
    const modalTitle = document.getElementById('modal-title');
    let allWorkers = [];

    loadWorkers();

    document.getElementById('add-btn').addEventListener('click', () => openModal());
    document.getElementById('cancel-btn').addEventListener('click', closeModal);
    document.getElementById('status-filter').addEventListener('change', renderFiltered);

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const dto = {
            name: document.getElementById('name').value.trim(),
            phone: document.getElementById('phone').value.trim(),
            assignedArea: document.getElementById('assignedArea').value.trim(),
            status: document.getElementById('status').value
        };
        const id = document.getElementById('workerId').value;

        try {
            if (id) {
                await apiPut(`/workers/${id}`, dto);
                showToast('Worker updated successfully');
            } else {
                await apiPost('/workers', dto);
                showToast('Worker added successfully');
            }
            closeModal();
            loadWorkers();
        } catch (err) {
            handleApiError(err);
        }
    });

    async function loadWorkers() {
        try {
            allWorkers = await apiGet('/workers');
            renderFiltered();
        } catch (err) {
            handleApiError(err);
        }
    }

    function renderFiltered() {
        const filter = document.getElementById('status-filter').value;
        const filtered = filter ? allWorkers.filter(w => w.status === filter) : allWorkers;
        renderTable(filtered);
    }

    function renderTable(workers) {
        const tbody = document.getElementById('workers-body');
        if (workers.length === 0) {
            tbody.innerHTML = `<tr class="empty-row"><td colspan="6">No workers found.</td></tr>`;
            return;
        }
        tbody.innerHTML = workers.map(w => `
            <tr>
                <td>#${w.workerId}</td>
                <td>${escapeHtml(w.name)}</td>
                <td>${escapeHtml(w.phone)}</td>
                <td>${escapeHtml(w.assignedArea)}</td>
                <td>${statusBadge(w.status)}</td>
                <td class="actions-cell">
                    <button class="btn btn-secondary btn-sm" onclick="editWorker(${w.workerId})">Edit</button>
                    <button class="btn btn-danger btn-sm" onclick="deleteWorker(${w.workerId})">Delete</button>
                </td>
            </tr>
        `).join('');
    }

    window.editWorker = function (id) {
        const worker = allWorkers.find(w => w.workerId === id);
        if (worker) openModal(worker);
    };

    window.deleteWorker = async function (id) {
        if (!confirm('Delete this worker?')) return;
        try {
            await apiDelete(`/workers/${id}`);
            showToast('Worker deleted');
            loadWorkers();
        } catch (err) {
            handleApiError(err);
        }
    };

    function openModal(worker) {
        form.reset();
        if (worker) {
            modalTitle.textContent = 'Edit Worker';
            document.getElementById('workerId').value = worker.workerId;
            document.getElementById('name').value = worker.name;
            document.getElementById('phone').value = worker.phone;
            document.getElementById('assignedArea').value = worker.assignedArea;
            document.getElementById('status').value = worker.status;
        } else {
            modalTitle.textContent = 'Add Worker';
            document.getElementById('workerId').value = '';
        }
        modal.classList.add('open');
    }

    function closeModal() {
        modal.classList.remove('open');
    }
})();
