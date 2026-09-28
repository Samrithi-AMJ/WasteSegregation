(function initReportsPage() {
    const pageBody = document.getElementById('page-body');
    pageBody.appendChild(document.getElementById('reports-template').content.cloneNode(true));

    let allCollections = [];
    let allComplaints = [];
    let allSegregations = [];
    let charts = {};

    document.getElementById('apply-filter-btn').addEventListener('click', renderAll);
    document.getElementById('reset-filter-btn').addEventListener('click', () => {
        document.getElementById('from-date').value = '';
        document.getElementById('to-date').value = '';
        document.getElementById('area-filter').value = '';
        document.getElementById('type-filter').value = '';
        renderAll();
    });

    init();

    async function init() {
        try {
            [allCollections, allComplaints, allSegregations] = await Promise.all([
                apiGet('/collections'),
                apiGet('/complaints'),
                apiGet('/segregations')
            ]);
            renderAll();
        } catch (err) {
            handleApiError(err);
        }
    }

    function getFilteredCollections() {
        const from = document.getElementById('from-date').value;
        const to = document.getElementById('to-date').value;
        const area = document.getElementById('area-filter').value.trim().toLowerCase();
        const type = document.getElementById('type-filter').value;

        return allCollections.filter(c => {
            if (from && c.collectionDate < from) return false;
            if (to && c.collectionDate > to) return false;
            if (area && !c.area.toLowerCase().includes(area)) return false;
            if (type && c.wasteType !== type) return false;
            return true;
        });
    }

    function renderAll() {
        const collections = getFilteredCollections();
        renderStatCards(collections);
        renderDailyChart(collections);
        renderWeeklyChart(collections);
        renderMonthlyChart(collections);
        renderTypeChart(collections);
        renderAreaTable(collections);
    }

    function renderStatCards(collections) {
        const totalWaste = collections.reduce((sum, c) => sum + c.quantity, 0);
        const totalCollections = collections.length;
        const collected = collections.filter(c => c.status === 'COLLECTED').length;
        const missed = collections.filter(c => c.status === 'MISSED').length;
        const completionRate = totalCollections > 0 ? (collected / totalCollections * 100) : 0;

        const collectionIds = new Set(collections.map(c => c.collectionId));
        const relevantSeg = allSegregations.filter(s => collectionIds.has(s.collectionId));
        const properTotal = relevantSeg.filter(s => s.segregationStatus === 'PROPER')
            .reduce((sum, s) => sum + s.totalQuantity, 0);
        const segRate = totalWaste > 0 ? (properTotal / totalWaste * 100) : 0;

        const totalComplaints = allComplaints.length;
        const resolvedComplaints = allComplaints.filter(c => c.status === 'RESOLVED').length;
        const resolutionRate = totalComplaints > 0 ? (resolvedComplaints / totalComplaints * 100) : 0;

        const cards = [
            { label: 'Total Waste (kg)', value: formatNumber(totalWaste) },
            { label: 'Total Collections', value: formatNumber(totalCollections) },
            { label: 'Collection Completion Rate', value: completionRate.toFixed(1) + '%' },
            { label: 'Missed Collections', value: formatNumber(missed), cls: 'danger' },
            { label: 'Segregation Rate', value: segRate.toFixed(1) + '%', cls: 'info' },
            { label: 'Complaint Resolution Rate', value: resolutionRate.toFixed(1) + '%' }
        ];
        document.getElementById('report-stat-grid').innerHTML = cards.map(c => `
            <div class="stat-card ${c.cls || ''}">
                <div class="stat-label">${c.label}</div>
                <div class="stat-value">${c.value}</div>
            </div>
        `).join('');
    }

    function destroyChart(key) {
        if (charts[key]) {
            charts[key].destroy();
        }
    }

    function renderDailyChart(collections) {
        const days = [];
        const today = new Date();
        for (let i = 13; i >= 0; i--) {
            const d = new Date(today);
            d.setDate(d.getDate() - i);
            days.push(d.toISOString().split('T')[0]);
        }
        const totals = days.map(day =>
            collections.filter(c => c.collectionDate === day).reduce((sum, c) => sum + c.quantity, 0));

        destroyChart('daily');
        charts.daily = new Chart(document.getElementById('chart-daily'), {
            type: 'line',
            data: {
                labels: days.map(d => d.slice(5)),
                datasets: [{ label: 'kg', data: totals, borderColor: '#2e7d32', backgroundColor: 'rgba(46,125,50,0.15)', fill: true, tension: 0.3 }]
            },
            options: { plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true } } }
        });
    }

    function renderWeeklyChart(collections) {
        const weeks = [];
        const today = new Date();
        for (let i = 7; i >= 0; i--) {
            const end = new Date(today);
            end.setDate(end.getDate() - (i * 7));
            const start = new Date(end);
            start.setDate(start.getDate() - 6);
            weeks.push({ start, end, label: `${start.getMonth() + 1}/${start.getDate()}` });
        }
        const totals = weeks.map(w => collections.filter(c => {
            const d = new Date(c.collectionDate);
            return d >= w.start && d <= w.end;
        }).reduce((sum, c) => sum + c.quantity, 0));

        destroyChart('weekly');
        charts.weekly = new Chart(document.getElementById('chart-weekly'), {
            type: 'bar',
            data: {
                labels: weeks.map(w => w.label),
                datasets: [{ label: 'kg', data: totals, backgroundColor: '#66bb6a' }]
            },
            options: { plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true } } }
        });
    }

    function renderMonthlyChart(collections) {
        const months = [];
        const today = new Date();
        for (let i = 5; i >= 0; i--) {
            const d = new Date(today.getFullYear(), today.getMonth() - i, 1);
            months.push({ year: d.getFullYear(), month: d.getMonth(), label: d.toLocaleString('en-IN', { month: 'short', year: '2-digit' }) });
        }
        const totals = months.map(m => collections.filter(c => {
            const d = new Date(c.collectionDate);
            return d.getFullYear() === m.year && d.getMonth() === m.month;
        }).reduce((sum, c) => sum + c.quantity, 0));

        destroyChart('monthly');
        charts.monthly = new Chart(document.getElementById('chart-monthly'), {
            type: 'bar',
            data: {
                labels: months.map(m => m.label),
                datasets: [{ label: 'kg', data: totals, backgroundColor: '#0288d1' }]
            },
            options: { plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true } } }
        });
    }

    function renderTypeChart(collections) {
        const types = ['WET', 'DRY', 'PLASTIC', 'PAPER', 'OTHER'];
        const totals = types.map(t => collections.filter(c => c.wasteType === t).reduce((sum, c) => sum + c.quantity, 0));

        destroyChart('type');
        charts.type = new Chart(document.getElementById('chart-type'), {
            type: 'doughnut',
            data: {
                labels: types,
                datasets: [{ data: totals, backgroundColor: ['#2e7d32', '#66bb6a', '#0288d1', '#f9a825', '#8e24aa'] }]
            },
            options: { plugins: { legend: { position: 'bottom' } } }
        });
    }

    function renderAreaTable(collections) {
        const areaMap = {};
        collections.forEach(c => {
            if (!areaMap[c.area]) {
                areaMap[c.area] = { totalWaste: 0, totalCollections: 0, missed: 0, ids: [] };
            }
            areaMap[c.area].totalWaste += c.quantity;
            areaMap[c.area].totalCollections += 1;
            areaMap[c.area].ids.push(c.collectionId);
            if (c.status === 'MISSED') areaMap[c.area].missed += 1;
        });

        const rows = Object.keys(areaMap).sort().map(area => {
            const data = areaMap[area];
            const idsSet = new Set(data.ids);
            const relevantSeg = allSegregations.filter(s => idsSet.has(s.collectionId));
            const properTotal = relevantSeg.filter(s => s.segregationStatus === 'PROPER')
                .reduce((sum, s) => sum + s.totalQuantity, 0);
            const segRate = data.totalWaste > 0 ? (properTotal / data.totalWaste * 100) : 0;
            return { area, ...data, segRate };
        });

        const tbody = document.getElementById('area-report-body');
        if (rows.length === 0) {
            tbody.innerHTML = `<tr class="empty-row"><td colspan="5">No data for the selected filters.</td></tr>`;
            return;
        }
        tbody.innerHTML = rows.map(r => `
            <tr>
                <td>${escapeHtml(r.area)}</td>
                <td>${formatNumber(r.totalWaste)}</td>
                <td>${r.totalCollections}</td>
                <td>${r.missed}</td>
                <td>${r.segRate.toFixed(1)}%</td>
            </tr>
        `).join('');
    }
})();
