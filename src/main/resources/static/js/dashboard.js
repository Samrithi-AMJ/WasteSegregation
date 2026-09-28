(async function initDashboard() {
    const pageBody = document.getElementById('page-body');
    const template = document.getElementById('dashboard-template');
    pageBody.appendChild(template.content.cloneNode(true));

    try {
        const [stats, wasteByType, collectionStatus, wasteByArea, complaintStatus,
               missed, collections, complaints] = await Promise.all([
            apiGet('/dashboard/statistics'),
            apiGet('/dashboard/waste-by-type'),
            apiGet('/dashboard/collection-status'),
            apiGet('/dashboard/waste-by-area'),
            apiGet('/dashboard/complaint-status'),
            apiGet('/dashboard/missed-collections'),
            apiGet('/collections'),
            apiGet('/complaints')
        ]);

        renderStatCards(stats);
        renderOverdueAlert(missed);
        renderWasteTypeChart(wasteByType);
        renderCollectionStatusChart(collectionStatus);
        renderWasteAreaChart(wasteByArea);
        renderComplaintStatusChart(complaintStatus);
        renderRecentCollections(collections);
        renderRecentComplaints(complaints);
        renderOverdueTable(missed);
    } catch (err) {
        handleApiError(err);
    }

    function renderStatCards(stats) {
        const cards = [
            { label: 'Total Waste (kg)', value: formatNumber(stats.totalWaste), cls: '' },
            { label: 'Total Collections', value: formatNumber(stats.totalCollections), cls: '' },
            { label: 'Wet Waste (kg)', value: formatNumber(stats.wetWaste), cls: '' },
            { label: 'Dry Waste (kg)', value: formatNumber(stats.dryWaste), cls: '' },
            { label: 'Segregation Rate', value: stats.segregationRate + '%', cls: 'info' },
            { label: 'Pending Collections', value: formatNumber(stats.pendingCollections), cls: 'warn' },
            { label: 'Missed Collections', value: formatNumber(stats.missedCollections), cls: 'danger' },
            { label: 'Open Complaints', value: formatNumber(stats.openComplaints), cls: 'warn' }
        ];
        document.getElementById('stat-grid').innerHTML = cards.map(c => `
            <div class="stat-card ${c.cls}">
                <div class="stat-label">${c.label}</div>
                <div class="stat-value">${c.value}</div>
            </div>
        `).join('');
    }

    function renderOverdueAlert(missed) {
        const box = document.getElementById('overdue-alert');
        if (missed.length > 0) {
            box.style.display = 'block';
            box.textContent = `⚠️ ${missed.length} collection(s) are currently overdue and need attention.`;
        }
    }

    function renderWasteTypeChart(data) {
        new Chart(document.getElementById('chart-waste-type'), {
            type: 'doughnut',
            data: {
                labels: Object.keys(data),
                datasets: [{
                    data: Object.values(data),
                    backgroundColor: ['#2e7d32', '#66bb6a', '#0288d1', '#f9a825', '#8e24aa']
                }]
            },
            options: { responsive: true, plugins: { legend: { position: 'bottom' } } }
        });
    }

    function renderCollectionStatusChart(data) {
        new Chart(document.getElementById('chart-collection-status'), {
            type: 'bar',
            data: {
                labels: Object.keys(data),
                datasets: [{ label: 'Collections', data: Object.values(data),
                    backgroundColor: ['#2e7d32', '#f9a825', '#d32f2f'] }]
            },
            options: { responsive: true, plugins: { legend: { display: false } },
                scales: { y: { beginAtZero: true, ticks: { precision: 0 } } } }
        });
    }

    function renderWasteAreaChart(data) {
        new Chart(document.getElementById('chart-waste-area'), {
            type: 'bar',
            data: {
                labels: data.map(a => a.area),
                datasets: [{ label: 'Waste (kg)', data: data.map(a => a.totalWaste), backgroundColor: '#66bb6a' }]
            },
            options: { responsive: true, plugins: { legend: { display: false } },
                scales: { y: { beginAtZero: true } } }
        });
    }

    function renderComplaintStatusChart(data) {
        new Chart(document.getElementById('chart-complaint-status'), {
            type: 'pie',
            data: {
                labels: Object.keys(data),
                datasets: [{ data: Object.values(data),
                    backgroundColor: ['#f9a825', '#0288d1', '#66bb6a', '#2e7d32', '#d32f2f'] }]
            },
            options: { responsive: true, plugins: { legend: { position: 'bottom' } } }
        });
    }

    function renderRecentCollections(collections) {
        const rows = collections
            .sort((a, b) => new Date(b.collectionDate) - new Date(a.collectionDate))
            .slice(0, 8);
        const tbody = document.getElementById('recent-collections-body');
        if (rows.length === 0) {
            tbody.innerHTML = `<tr class="empty-row"><td colspan="8">No collections recorded yet.</td></tr>`;
            return;
        }
        tbody.innerHTML = rows.map(c => `
            <tr>
                <td>#${c.collectionId}</td>
                <td>${escapeHtml(c.area)}</td>
                <td>${c.wasteType}</td>
                <td>${formatNumber(c.quantity)}</td>
                <td>${escapeHtml(c.workerName || '-')}</td>
                <td>${escapeHtml(c.vehicleNumber || '-')}</td>
                <td>${formatDate(c.collectionDate)}</td>
                <td>${statusBadge(c.status)}</td>
            </tr>
        `).join('');
    }

    function renderRecentComplaints(complaints) {
        const rows = complaints
            .sort((a, b) => new Date(b.complaintDate) - new Date(a.complaintDate))
            .slice(0, 8);
        const tbody = document.getElementById('recent-complaints-body');
        if (rows.length === 0) {
            tbody.innerHTML = `<tr class="empty-row"><td colspan="6">No complaints recorded yet.</td></tr>`;
            return;
        }
        tbody.innerHTML = rows.map(c => `
            <tr>
                <td>#${c.complaintId}</td>
                <td>${escapeHtml(c.citizenName)}</td>
                <td>${escapeHtml(c.location)}</td>
                <td>${escapeHtml(c.description)}</td>
                <td>${formatDate(c.complaintDate)}</td>
                <td>${statusBadge(c.status)}</td>
            </tr>
        `).join('');
    }

    function renderOverdueTable(missed) {
        const tbody = document.getElementById('overdue-body');
        if (missed.length === 0) {
            tbody.innerHTML = `<tr class="empty-row"><td colspan="5">No overdue collections. Great job!</td></tr>`;
            return;
        }
        tbody.innerHTML = missed.map(m => `
            <tr>
                <td>${escapeHtml(m.area)}</td>
                <td>${escapeHtml(m.citizenName)}</td>
                <td>${formatDate(m.lastCollectionDate)}</td>
                <td>${m.daysOverdue} day(s)</td>
                <td>${statusBadge(m.status)}</td>
            </tr>
        `).join('');
    }
})();
