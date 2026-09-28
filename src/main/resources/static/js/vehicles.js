(function initVehiclesPage() {
    const pageBody = document.getElementById('page-body');
    pageBody.appendChild(document.getElementById('vehicles-template').content.cloneNode(true));

    const modal = document.getElementById('vehicle-modal');
    const form = document.getElementById('vehicle-form');
    const modalTitle = document.getElementById('modal-title');
    let allVehicles = [];

    loadVehicles();

    document.getElementById('add-btn').addEventListener('click', () => openModal());
    document.getElementById('cancel-btn').addEventListener('click', closeModal);
    document.getElementById('status-filter').addEventListener('change', renderFiltered);

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const dto = {
            vehicleNumber: document.getElementById('vehicleNumber').value.trim(),
            vehicleType: document.getElementById('vehicleType').value,
            driverName: document.getElementById('driverName').value.trim(),
            capacity: Number(document.getElementById('capacity').value),
            status: document.getElementById('status').value
        };
        const id = document.getElementById('vehicleId').value;

        try {
            if (id) {
                await apiPut(`/vehicles/${id}`, dto);
                showToast('Vehicle updated successfully');
            } else {
                await apiPost('/vehicles', dto);
                showToast('Vehicle added successfully');
            }
            closeModal();
            loadVehicles();
        } catch (err) {
            handleApiError(err);
        }
    });

    async function loadVehicles() {
        try {
            allVehicles = await apiGet('/vehicles');
            renderFiltered();
        } catch (err) {
            handleApiError(err);
        }
    }

    function renderFiltered() {
        const filter = document.getElementById('status-filter').value;
        const filtered = filter ? allVehicles.filter(v => v.status === filter) : allVehicles;
        renderTable(filtered);
    }

    function renderTable(vehicles) {
        const tbody = document.getElementById('vehicles-body');
        if (vehicles.length === 0) {
            tbody.innerHTML = `<tr class="empty-row"><td colspan="7">No vehicles found.</td></tr>`;
            return;
        }
        tbody.innerHTML = vehicles.map(v => `
            <tr>
                <td>#${v.vehicleId}</td>
                <td>${escapeHtml(v.vehicleNumber)}</td>
                <td>${v.vehicleType.replace('_', ' ')}</td>
                <td>${escapeHtml(v.driverName)}</td>
                <td>${formatNumber(v.capacity)}</td>
                <td>${statusBadge(v.status)}</td>
                <td class="actions-cell">
                    <button class="btn btn-secondary btn-sm" onclick="editVehicle(${v.vehicleId})">Edit</button>
                    <button class="btn btn-danger btn-sm" onclick="deleteVehicle(${v.vehicleId})">Delete</button>
                </td>
            </tr>
        `).join('');
    }

    window.editVehicle = function (id) {
        const vehicle = allVehicles.find(v => v.vehicleId === id);
        if (vehicle) openModal(vehicle);
    };

    window.deleteVehicle = async function (id) {
        if (!confirm('Delete this vehicle?')) return;
        try {
            await apiDelete(`/vehicles/${id}`);
            showToast('Vehicle deleted');
            loadVehicles();
        } catch (err) {
            handleApiError(err);
        }
    };

    function openModal(vehicle) {
        form.reset();
        if (vehicle) {
            modalTitle.textContent = 'Edit Vehicle';
            document.getElementById('vehicleId').value = vehicle.vehicleId;
            document.getElementById('vehicleNumber').value = vehicle.vehicleNumber;
            document.getElementById('vehicleType').value = vehicle.vehicleType;
            document.getElementById('driverName').value = vehicle.driverName;
            document.getElementById('capacity').value = vehicle.capacity;
            document.getElementById('status').value = vehicle.status;
        } else {
            modalTitle.textContent = 'Add Vehicle';
            document.getElementById('vehicleId').value = '';
        }
        modal.classList.add('open');
    }

    function closeModal() {
        modal.classList.remove('open');
    }
})();
