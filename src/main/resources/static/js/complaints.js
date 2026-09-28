
(function initComplaintsPage() {
    const pageBody = document.getElementById('page-body');
    pageBody.appendChild(
        document.getElementById('complaints-template').content.cloneNode(true)
    );

    const isCitizen = CURRENT_USER.role === 'CITIZEN';

    const modal = document.getElementById('complaint-modal');
    const form = document.getElementById('complaint-form');
    const modalTitle = document.getElementById('modal-title');
    const citizenGroup = document.getElementById('citizen-select-group');
    const statusGroup = document.getElementById('status-group');

    const citizenSelect = document.getElementById('citizenId');
    const locationInput = document.getElementById('location');
    const descriptionInput = document.getElementById('description');
    const statusSelect = document.getElementById('status');

    let allComplaints = [];
    let citizens = [];

    document.getElementById('panel-heading').textContent =
        isCitizen ? 'My Complaints' : 'All Complaints';

    if (isCitizen) {
        document.getElementById('add-btn').textContent = '+ Submit Complaint';
    }

    init();

    document.getElementById('add-btn').addEventListener('click', () => {
        openModal();
    });

    document.getElementById('cancel-btn').addEventListener('click', closeModal);

    document
        .getElementById('status-filter')
        .addEventListener('change', renderFiltered);

    // ============================================
    // FORM SUBMIT
    // ============================================

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const id = document.getElementById('complaintId').value;

        try {
            if (id) {

                // ADMIN: Update complaint status
                await apiPatch(
                    `/complaints/${id}/status`,
                    {
                        status: statusSelect.value
                    }
                );

                showToast('Complaint status updated');

            } else {

                // CREATE NEW COMPLAINT
                const dto = {
                    citizenId: isCitizen
                        ? CURRENT_USER.citizenId
                        : Number(citizenSelect.value),

                    location: locationInput.value.trim(),

                    description: descriptionInput.value.trim()
                };

                // Basic validation
                if (!dto.location) {
                    showToast('Please enter the complaint location');
                    locationInput.focus();
                    return;
                }

                if (!dto.description) {
                    showToast('Please enter the complaint description');
                    descriptionInput.focus();
                    return;
                }

                if (!dto.citizenId) {
                    showToast('Citizen information is missing');
                    return;
                }

                await apiPost('/complaints', dto);

                showToast('Complaint submitted successfully');
            }

            closeModal();

            await loadComplaints();

        } catch (err) {
            console.error('Complaint operation failed:', err);
            handleApiError(err);
        }
    });

    // ============================================
    // INITIALIZE
    // ============================================

    async function init() {
        try {

            // Only ADMIN needs the citizen dropdown
            if (!isCitizen) {

                citizens = await apiGet('/citizens');

                citizenSelect.innerHTML = citizens
                    .map(c =>
                        `<option value="${c.citizenId}">
                            ${escapeHtml(c.name)}
                        </option>`
                    )
                    .join('');
            }

            await loadComplaints();

        } catch (err) {
            console.error('Failed to initialize complaints:', err);
            handleApiError(err);
        }
    }

    // ============================================
    // LOAD COMPLAINTS
    // ============================================

    async function loadComplaints() {
        try {

            const path = isCitizen
                ? `/complaints?citizenId=${CURRENT_USER.citizenId}`
                : '/complaints';

            allComplaints = await apiGet(path);

            renderFiltered();

        } catch (err) {
            console.error('Failed to load complaints:', err);
            handleApiError(err);
        }
    }

    // ============================================
    // FILTER
    // ============================================

    function renderFiltered() {

        const filter =
            document.getElementById('status-filter').value;

        const filtered = filter
            ? allComplaints.filter(c => c.status === filter)
            : allComplaints;

        renderTable(filtered);
    }

    // ============================================
    // TABLE
    // ============================================

    function renderTable(complaints) {

        const tbody =
            document.getElementById('complaints-body');

        if (complaints.length === 0) {

            tbody.innerHTML = `
                <tr class="empty-row">
                    <td colspan="7">
                        No complaints found.
                    </td>
                </tr>
            `;

            return;
        }

        tbody.innerHTML = complaints
            .sort(
                (a, b) =>
                    new Date(b.complaintDate) -
                    new Date(a.complaintDate)
            )
            .map(c => `
                <tr>
                    <td>#${c.complaintId}</td>

                    <td>
                        ${escapeHtml(c.citizenName || '')}
                    </td>

                    <td>
                        ${escapeHtml(c.location || '')}
                    </td>

                    <td>
                        ${escapeHtml(c.description || '')}
                    </td>

                    <td>
                        ${formatDate(c.complaintDate)}
                    </td>

                    <td>
                        ${statusBadge(c.status)}
                    </td>

                    <td class="actions-cell">

                        ${
                            isCitizen
                                ? ''
                                : `
                                    <button
                                        class="btn btn-secondary btn-sm"
                                        onclick="editComplaint(${c.complaintId})">
                                        Update Status
                                    </button>

                                    <button
                                        class="btn btn-danger btn-sm"
                                        onclick="deleteComplaint(${c.complaintId})">
                                        Delete
                                    </button>
                                `
                        }

                    </td>
                </tr>
            `)
            .join('');
    }

    // ============================================
    // EDIT COMPLAINT
    // ============================================

    window.editComplaint = function (id) {

        const complaint =
            allComplaints.find(
                c => c.complaintId === id
            );

        if (complaint) {
            openModal(complaint);
        }
    };

    // ============================================
    // DELETE COMPLAINT
    // ============================================

    window.deleteComplaint = async function (id) {

        if (!confirm('Delete this complaint?')) {
            return;
        }

        try {

            await apiDelete(`/complaints/${id}`);

            showToast('Complaint deleted');

            await loadComplaints();

        } catch (err) {

            console.error('Delete failed:', err);

            handleApiError(err);
        }
    };

    // ============================================
    // OPEN MODAL
    // ============================================

    function openModal(complaint) {

        form.reset();

        // ----------------------------------------
        // EDIT EXISTING COMPLAINT
        // ----------------------------------------

        if (complaint) {

            modalTitle.textContent =
                `Update Complaint #${complaint.complaintId}`;

            document.getElementById('complaintId').value =
                complaint.complaintId;

            locationInput.value =
                complaint.location || '';

            descriptionInput.value =
                complaint.description || '';

            statusSelect.value =
                complaint.status || 'PENDING';

            // Hide citizen selector
            citizenGroup.style.display = 'none';

            // Show status selector
            statusGroup.style.display = 'block';

            // Existing complaint details are read-only
            locationInput.disabled = true;
            descriptionInput.disabled = true;

            locationInput.required = false;
            descriptionInput.required = false;

            // Citizen selector is hidden
            citizenSelect.disabled = true;
            citizenSelect.required = false;

            // Status is active
            statusSelect.disabled = false;
            statusSelect.required = true;

        }

        // ----------------------------------------
        // CREATE NEW COMPLAINT
        // ----------------------------------------

        else {

            modalTitle.textContent =
                isCitizen
                    ? 'Submit a Complaint'
                    : 'New Complaint';

            document.getElementById('complaintId').value = '';

            // Citizen does not select themselves.
            // Backend gets CURRENT_USER.citizenId.
            if (isCitizen) {

                citizenGroup.style.display = 'none';

                citizenSelect.disabled = true;
                citizenSelect.required = false;

            }

            // Admin selects a citizen.
            else {

                citizenGroup.style.display = 'block';

                citizenSelect.disabled = false;
                citizenSelect.required = true;
            }

            // Status isn't needed while creating
            statusGroup.style.display = 'none';

            statusSelect.disabled = true;
            statusSelect.required = false;

            // Enable complaint fields
            locationInput.disabled = false;
            descriptionInput.disabled = false;

            locationInput.required = true;
            descriptionInput.required = true;
        }

        modal.classList.add('open');
    }

    // ============================================
    // CLOSE MODAL
    // ============================================

    function closeModal() {

        modal.classList.remove('open');

        // Reset disabled/required state
        locationInput.disabled = false;
        descriptionInput.disabled = false;

        locationInput.required = true;
        descriptionInput.required = true;

        citizenSelect.disabled = false;
        citizenSelect.required = !isCitizen;

        statusSelect.disabled = true;
        statusSelect.required = false;
    }

})();

