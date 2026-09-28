(function initCitizensPage() {
    const pageBody = document.getElementById('page-body');
    pageBody.appendChild(document.getElementById('citizens-template').content.cloneNode(true));

    const modal = document.getElementById('citizen-modal');
    const form = document.getElementById('citizen-form');
    const modalTitle = document.getElementById('modal-title');
    let allCitizens = [];

    loadCitizens();

    document.getElementById('add-btn').addEventListener('click', () => openModal());
    document.getElementById('cancel-btn').addEventListener('click', closeModal);
    document.getElementById('search-input').addEventListener('input', debounce(loadCitizens, 300));

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const dto = {
            name: document.getElementById('name').value.trim(),
            phone: document.getElementById('phone').value.trim(),
            email: document.getElementById('email').value.trim(),
            address: document.getElementById('address').value.trim(),
            area: document.getElementById('area').value.trim(),
            wardNumber: document.getElementById('wardNumber').value ? Number(document.getElementById('wardNumber').value) : null
        };
        const id = document.getElementById('citizenId').value;

        try {
            if (id) {
                await apiPut(`/citizens/${id}`, dto);
                showToast('Citizen updated successfully');
            } else {
                await apiPost('/citizens', dto);
                showToast('Citizen added successfully');
            }
            closeModal();
            loadCitizens();
        } catch (err) {
            handleApiError(err);
        }
    });

    async function loadCitizens() {
        const search = document.getElementById('search-input').value.trim();
        try {
            const path = search ? `/citizens?search=${encodeURIComponent(search)}` : '/citizens';
            allCitizens = await apiGet(path);
            renderTable(allCitizens);
        } catch (err) {
            handleApiError(err);
        }
    }

    function renderTable(citizens) {
        const tbody = document.getElementById('citizens-body');
        if (citizens.length === 0) {
            tbody.innerHTML = `<tr class="empty-row"><td colspan="8">No citizens found.</td></tr>`;
            return;
        }
        tbody.innerHTML = citizens.map(c => `
            <tr>
                <td>#${c.citizenId}</td>
                <td>${escapeHtml(c.name)}</td>
                <td>${escapeHtml(c.phone)}</td>
                <td>${escapeHtml(c.email)}</td>
                <td>${escapeHtml(c.address)}</td>
                <td>${escapeHtml(c.area)}</td>
                <td>${c.wardNumber ?? '-'}</td>
                <td class="actions-cell">
                    <button class="btn btn-secondary btn-sm" onclick="editCitizen(${c.citizenId})">Edit</button>
                    <button class="btn btn-danger btn-sm" onclick="deleteCitizen(${c.citizenId})">Delete</button>
                </td>
            </tr>
        `).join('');
    }

    window.editCitizen = function (id) {
        const citizen = allCitizens.find(c => c.citizenId === id);
        if (citizen) openModal(citizen);
    };

    window.deleteCitizen = async function (id) {
        if (!confirm('Delete this citizen and all related records?')) return;
        try {
            await apiDelete(`/citizens/${id}`);
            showToast('Citizen deleted');
            loadCitizens();
        } catch (err) {
            handleApiError(err);
        }
    };

    function openModal(citizen) {
        form.reset();
        if (citizen) {
            modalTitle.textContent = 'Edit Citizen';
            document.getElementById('citizenId').value = citizen.citizenId;
            document.getElementById('name').value = citizen.name;
            document.getElementById('phone').value = citizen.phone;
            document.getElementById('email').value = citizen.email;
            document.getElementById('address').value = citizen.address;
            document.getElementById('area').value = citizen.area;
            document.getElementById('wardNumber').value = citizen.wardNumber || '';
        } else {
            modalTitle.textContent = 'Add Citizen';
            document.getElementById('citizenId').value = '';
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
