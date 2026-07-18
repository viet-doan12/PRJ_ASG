<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reports - Admin Dashboard</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>

    <style>
        :root {
            --primary-color: #4f46e5;
            --primary-hover: #4338ca;
            --secondary-bg: #f9fafb;
            --sidebar-bg: #1e1b4b;
            --sidebar-hover: #312e81;
            --text-main: #111827;
            --text-muted: #6b7280;
            --border-color: #e5e7eb;
            --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
        }
        body { font-family: 'Inter', sans-serif; background-color: var(--secondary-bg); color: var(--text-main); overflow-x: hidden; }
        .sidebar { background-color: var(--sidebar-bg); min-height: 100vh; color: #ffffff; }
        .sidebar .brand { padding: 1.5rem 1rem; font-size: 1.25rem; font-weight: 700; border-bottom: 1px solid rgba(255,255,255,0.1); color: #38bdf8; }
        .sidebar-menu { list-style: none; padding: 1rem 0; margin: 0; }
        .sidebar-menu li a { display: flex; align-items: center; padding: 0.75rem 1.5rem; color: #cbd5e1; text-decoration: none; border-left: 4px solid transparent; }
        .sidebar-menu li a:hover, .sidebar-menu li.active a { background-color: var(--sidebar-hover); color: #ffffff; border-left-color: var(--primary-color); }
        .sidebar-menu li a i { margin-right: 0.75rem; width: 20px; text-align: center; }
        .main-content { padding: 2rem; min-height: 100vh; }
        .page-title { font-size: 1.75rem; font-weight: 700; margin: 0; }
        .card { background: #ffffff; border: 1px solid var(--border-color); border-radius: 12px; box-shadow: var(--shadow-sm); margin-bottom: 1.5rem; }
        .stat-card { padding: 1.25rem 1.5rem; }
        .stat-value { font-size: 1.5rem; font-weight: 700; color: var(--primary-color); }
        .stat-label { color: var(--text-muted); font-size: 0.85rem; }
        .report-tabs a { padding: 0.6rem 1.1rem; border-radius: 8px; text-decoration: none; color: var(--text-muted); font-weight: 500; font-size: 0.9rem; }
        .report-tabs a.active { background-color: var(--primary-color); color: #fff; }
        .table th { background-color: #f8fafc; color: var(--text-muted); font-weight: 600; text-transform: uppercase; font-size: 0.75rem; padding: 1rem 1.5rem; }
        .table td { padding: 1rem 1.5rem; vertical-align: middle; font-size: 0.875rem; }
        .filter-panel { background: #ffffff; border: 1px solid var(--border-color); border-radius: 12px; padding: 1.25rem 1.5rem; margin-bottom: 1.5rem; }
        .badge-stock-out { background-color: #fee2e2; color: #b91c1c; font-weight: 500; padding: 0.35em 0.65em; border-radius: 9999px; font-size: 0.75rem; }
        .badge-stock-low { background-color: #fef3c7; color: #b45309; font-weight: 500; padding: 0.35em 0.65em; border-radius: 9999px; font-size: 0.75rem; }
        .chart-wrapper { padding: 1.5rem; height: 340px; }
    </style>
</head>
<body>

<div class="container-fluid p-0">
    <div class="row g-0">
        <div class="col-md-3 col-lg-2 sidebar">
            <div class="brand"><i class="fa-solid fa-seedling me-2"></i> FlowerShop Admin</div>
            <ul class="sidebar-menu">
                <li><a href="${pageContext.request.contextPath}/admin/dashboard"><i class="fa-solid fa-chart-line"></i> Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/users"><i class="fa-solid fa-users"></i> Users</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/categories"><i class="fa-solid fa-tags"></i> Categories</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/flowers"><i class="fa-solid fa-leaf"></i> Flowers</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/orders"><i class="fa-solid fa-cart-shopping"></i> Orders</a></li>
                <li class="active"><a href="${pageContext.request.contextPath}/admin/reports"><i class="fa-solid fa-file-invoice-dollar"></i> Reports</a></li>
                <li class="mt-5"><a href="${pageContext.request.contextPath}/logout" class="text-danger"><i class="fa-solid fa-right-from-bracket text-danger"></i> Logout</a></li>
            </ul>
        </div>

        <div class="col-md-9 col-lg-10 main-content">
            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h1 class="page-title">Reports &amp; Statistics</h1>
                    <p class="text-muted mb-0">Thống kê tổng quan hệ thống bán hoa</p>
                </div>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-danger border-0 shadow-sm"><i class="fa-solid fa-triangle-exclamation me-2"></i>${error}</div>
            </c:if>

            <!-- Quick stats -->
            <div class="row g-3 mb-4">
                <div class="col-md-4">
                    <div class="card stat-card">
                        <div class="stat-label">Tổng doanh thu (toàn hệ thống)</div>
                        <div class="stat-value">${totalRevenueDisplay} đ</div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card stat-card">
                        <div class="stat-label">Doanh thu (theo bộ lọc hiện tại)</div>
                        <div class="stat-value">${periodRevenueDisplay} đ</div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card stat-card">
                        <div class="stat-label">Số đơn hàng (theo bộ lọc hiện tại)</div>
                        <div class="stat-value">${periodOrderCount}</div>
                    </div>
                </div>
            </div>

            <!-- Report type tabs -->
            <div class="d-flex gap-2 flex-wrap report-tabs mb-4">
                <a href="${pageContext.request.contextPath}/admin/reports?type=revenue" class="${reportType == 'revenue' ? 'active' : ''}">
                    <i class="fa-solid fa-sack-dollar me-1"></i> Doanh thu
                </a>
                <a href="${pageContext.request.contextPath}/admin/reports?type=topselling" class="${reportType == 'topselling' ? 'active' : ''}">
                    <i class="fa-solid fa-fire me-1"></i> Sản phẩm bán chạy
                </a>
                <a href="${pageContext.request.contextPath}/admin/reports?type=inventory" class="${reportType == 'inventory' ? 'active' : ''}">
                    <i class="fa-solid fa-boxes-stacked me-1"></i> Tồn kho
                </a>
                <a href="${pageContext.request.contextPath}/admin/reports?type=customer" class="${reportType == 'customer' ? 'active' : ''}">
                    <i class="fa-solid fa-crown me-1"></i> Khách hàng VIP
                </a>
                <a href="${pageContext.request.contextPath}/admin/reports?type=orderstatus" class="${reportType == 'orderstatus' ? 'active' : ''}">
                    <i class="fa-solid fa-list-check me-1"></i> Đơn hàng theo trạng thái
                </a>
            </div>

            <!-- ===== BỘ LỌC RIÊNG CHO TỪNG LOẠI BÁO CÁO ===== -->
            <c:if test="${reportType == 'revenue'}">
                <div class="filter-panel">
                    <form method="get" action="${pageContext.request.contextPath}/admin/reports" class="row g-2 align-items-end">
                        <input type="hidden" name="type" value="revenue">

                        <div class="col-auto">
                            <label class="form-label mb-0 fw-semibold small">Xem theo</label>
                            <select name="granularity" id="granularitySelect" class="form-select form-select-sm" onchange="toggleGranularity(); this.form.submit();">
                                <option value="day" ${granularity == 'day' ? 'selected' : ''}>Ngày (trong 1 tháng)</option>
                                <option value="month" ${granularity == 'month' ? 'selected' : ''}>Tháng (trong 1 năm)</option>
                                <option value="year" ${granularity == 'year' ? 'selected' : ''}>Năm (toàn bộ)</option>
                            </select>
                        </div>

                        <div class="col-auto" id="yearGroup" style="${granularity == 'year' ? 'display:none;' : ''}">
                            <label class="form-label mb-0 fw-semibold small">Năm</label>
                            <select name="year" class="form-select form-select-sm" onchange="this.form.submit()">
                                <c:forEach var="y" begin="${currentYear - 4}" end="${currentYear}">
                                    <option value="${y}" ${y == year ? 'selected' : ''}>${y}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="col-auto" id="monthGroup" style="${granularity == 'day' ? '' : 'display:none;'}">
                            <label class="form-label mb-0 fw-semibold small">Tháng</label>
                            <select name="month" class="form-select form-select-sm" onchange="this.form.submit()">
                                <c:forEach var="m" begin="1" end="12">
                                    <option value="${m}" ${m == month ? 'selected' : ''}>Tháng ${m}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="col-auto ms-auto">
                            <label class="form-label mb-0 fw-semibold small">Xuất báo cáo (theo khoảng ngày)</label>
                            <select name="range" id="rangeSelect" class="form-select form-select-sm" onchange="toggleCustomRange()">
                                <option value="day">Hôm nay</option>
                                <option value="week">Tuần này</option>
                                <option value="month" selected>Tháng này</option>
                                <option value="custom">Tùy chọn</option>
                            </select>
                        </div>

                        <div class="col-auto" id="customRangeFrom" style="display:none;">
                            <label class="form-label mb-0 small">Từ ngày</label>
                            <input type="date" id="startDateInput" class="form-control form-control-sm">
                        </div>
                        <div class="col-auto" id="customRangeTo" style="display:none;">
                            <label class="form-label mb-0 small">Đến ngày</label>
                            <input type="date" id="endDateInput" class="form-control form-control-sm">
                        </div>

                        <div class="col-auto d-flex gap-2">
                            <button type="button" class="btn btn-sm btn-success" onclick="doExport('csv')">
                                <i class="fa-solid fa-file-csv me-1"></i> Xuất Excel (CSV)
                            </button>
                            <button type="button" class="btn btn-sm btn-danger" onclick="doExport('print')">
                                <i class="fa-solid fa-file-pdf me-1"></i> In / Xuất PDF
                            </button>
                        </div>
                    </form>
                </div>
            </c:if>

            <c:if test="${reportType == 'topselling'}">
                <div class="filter-panel">
                    <form method="get" action="${pageContext.request.contextPath}/admin/reports" class="row g-2 align-items-end">
                        <input type="hidden" name="type" value="topselling">
                        <div class="col-auto">
                            <label class="form-label mb-0 fw-semibold small">Danh mục</label>
                            <select name="categoryId" class="form-select form-select-sm" onchange="this.form.submit()">
                                <option value="0" ${categoryId == 0 ? 'selected' : ''}>Tất cả danh mục</option>
                                <c:forEach var="cat" items="${categoryList}">
                                    <option value="${cat.categoryID}" ${categoryId == cat.categoryID ? 'selected' : ''}>${cat.categoryName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-auto">
                            <label class="form-label mb-0 fw-semibold small">Số lượng hiển thị</label>
                            <select name="limit" class="form-select form-select-sm" onchange="this.form.submit()">
                                <option value="5" ${limit == 5 ? 'selected' : ''}>Top 5</option>
                                <option value="10" ${limit == 10 ? 'selected' : ''}>Top 10</option>
                                <option value="20" ${limit == 20 ? 'selected' : ''}>Top 20</option>
                            </select>
                        </div>
                    </form>
                </div>
            </c:if>

            <!-- ===== BIỂU ĐỒ ===== -->
            <div class="card">
                <div class="chart-wrapper">
                    <canvas id="reportChart"></canvas>
                </div>
            </div>

            <!-- ===== BẢNG DỮ LIỆU CHI TIẾT ===== -->
            <div class="card">
                <div class="table-responsive">
                    <table class="table align-middle mb-0">
                        <c:choose>
                            <c:when test="${reportType == 'revenue'}">
                                <thead>
                                    <tr>
                                        <th>
                                            <c:choose>
                                                <c:when test="${granularity == 'day'}">Ngày</c:when>
                                                <c:when test="${granularity == 'year'}">Năm</c:when>
                                                <c:otherwise>Tháng</c:otherwise>
                                            </c:choose>
                                        </th>
                                        <th class="text-end">Số đơn hàng</th>
                                        <th class="text-end">Doanh thu (VNĐ)</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty reportData}">
                                            <tr><td colspan="3" class="text-center py-5 text-muted">Không có dữ liệu.</td></tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="row" items="${reportData}">
                                                <tr>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${granularity == 'day'}">Ngày ${row.dayNum}</c:when>
                                                            <c:when test="${granularity == 'year'}">${row.yearNum}</c:when>
                                                            <c:otherwise>Tháng ${row.month}</c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td class="text-end">${row.orderCount}</td>
                                                    <td class="text-end fw-semibold">${row.revenueDisplay}</td>
                                                </tr>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </c:when>

                            <c:when test="${reportType == 'topselling'}">
                                <thead><tr><th>Sản phẩm</th><th class="text-end">Số lượng đã bán</th><th class="text-end">Doanh thu (VNĐ)</th></tr></thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty reportData}">
                                            <tr><td colspan="3" class="text-center py-5 text-muted">Chưa có dữ liệu bán hàng.</td></tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="row" items="${reportData}">
                                                <tr>
                                                    <td class="fw-semibold">${row.flowerName}</td>
                                                    <td class="text-end">${row.totalSold}</td>
                                                    <td class="text-end fw-semibold">${row.revenueDisplay}</td>
                                                </tr>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </c:when>

                            <c:when test="${reportType == 'inventory'}">
                                <thead><tr><th>Sản phẩm</th><th class="text-end">Tồn kho</th><th style="width:140px;">Cảnh báo</th></tr></thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty reportData}">
                                            <tr><td colspan="3" class="text-center py-5 text-muted">Không có sản phẩm nào dưới ngưỡng tồn kho.</td></tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="row" items="${reportData}">
                                                <tr>
                                                    <td class="fw-semibold">${row.flowerName}</td>
                                                    <td class="text-end">${row.stockQuantity}</td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${row.stockQuantity <= 0}"><span class="badge-stock-out">Hết hàng</span></c:when>
                                                            <c:otherwise><span class="badge-stock-low">Sắp hết</span></c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </c:when>

                            <c:when test="${reportType == 'customer'}">
                                <thead><tr><th>Khách hàng</th><th class="text-end">Số đơn hàng</th><th class="text-end">Tổng chi tiêu (VNĐ)</th></tr></thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty reportData}">
                                            <tr><td colspan="3" class="text-center py-5 text-muted">Chưa có dữ liệu khách hàng.</td></tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="row" items="${reportData}">
                                                <tr>
                                                    <td class="fw-semibold">${row.fullName}</td>
                                                    <td class="text-end">${row.orderCount}</td>
                                                    <td class="text-end fw-semibold">${row.totalSpentDisplay}</td>
                                                </tr>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </c:when>

                            <c:when test="${reportType == 'orderstatus'}">
                                <thead><tr><th>Trạng thái</th><th class="text-end">Số lượng đơn</th></tr></thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty reportData}">
                                            <tr><td colspan="2" class="text-center py-5 text-muted">Chưa có đơn hàng nào.</td></tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="row" items="${reportData}">
                                                <tr><td class="fw-semibold">${row.status}</td><td class="text-end">${row.orderCount}</td></tr>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </c:when>
                        </c:choose>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    function toggleGranularity() {
        var g = document.getElementById('granularitySelect').value;
        document.getElementById('yearGroup').style.display = (g === 'year') ? 'none' : 'block';
        document.getElementById('monthGroup').style.display = (g === 'day') ? 'block' : 'none';
    }

    function toggleCustomRange() {
        var isCustom = document.getElementById('rangeSelect').value === 'custom';
        document.getElementById('customRangeFrom').style.display = isCustom ? 'block' : 'none';
        document.getElementById('customRangeTo').style.display = isCustom ? 'block' : 'none';
    }

    function doExport(format) {
        var range = document.getElementById('rangeSelect').value;
        var url = '${pageContext.request.contextPath}/admin/reports?format=' + format + '&range=' + range;
        if (range === 'custom') {
            var start = document.getElementById('startDateInput').value;
            var end = document.getElementById('endDateInput').value;
            if (!start || !end) { alert('Vui lòng chọn đầy đủ Từ ngày và Đến ngày.'); return; }
            url += '&startDate=' + start + '&endDate=' + end;
        }
        window.open(url, '_blank');
    }

    // ===== DỰNG BIỂU ĐỒ THEO ĐÚNG LOẠI BÁO CÁO ĐANG XEM =====
    (function () {
        var reportType = '${reportType}';
        var granularity = '${granularity}';
        var labels = [];
        var values = [];
        var secondaryValues = [];

        <c:forEach var="row" items="${reportData}">
            <c:choose>
                <c:when test="${reportType == 'revenue'}">
                    <c:choose>
                        <c:when test="${granularity == 'day'}">labels.push('Ngày ${row.dayNum}');</c:when>
                        <c:when test="${granularity == 'year'}">labels.push('${row.yearNum}');</c:when>
                        <c:otherwise>labels.push('Tháng ${row.month}');</c:otherwise>
                    </c:choose>
                    values.push(${row.revenue});
                </c:when>
                <c:when test="${reportType == 'topselling'}">
                    labels.push('${row.flowerName}');
                    values.push(${row.totalSold});
                    secondaryValues.push(${row.revenue});
                </c:when>
                <c:when test="${reportType == 'inventory'}">
                    labels.push('${row.flowerName}');
                    values.push(${row.stockQuantity});
                </c:when>
                <c:when test="${reportType == 'customer'}">
                    labels.push('${row.fullName}');
                    values.push(${row.totalSpent});
                </c:when>
                <c:when test="${reportType == 'orderstatus'}">
                    labels.push('${row.status}');
                    values.push(${row.orderCount});
                </c:when>
            </c:choose>
        </c:forEach>

        var ctx = document.getElementById('reportChart').getContext('2d');
        var chartType = (reportType === 'orderstatus') ? 'pie' : 'bar';
        var chartLabel = {
            revenue: 'Doanh thu (VNĐ)',
            topselling: 'Số lượng đã bán',
            inventory: 'Tồn kho',
            customer: 'Tổng chi tiêu (VNĐ)',
            orderstatus: 'Số lượng đơn'
        }[reportType] || 'Giá trị';

        var palette = ['#4f46e5', '#198754', '#d63384', '#fd7e14', '#0dcaf0', '#6f42c1', '#20c997', '#dc3545'];

        new Chart(ctx, {
            type: chartType,
            data: {
                labels: labels,
                datasets: [{
                    label: chartLabel,
                    data: values,
                    backgroundColor: chartType === 'pie' ? palette : '#4f46e5',
                    borderRadius: chartType === 'bar' ? 6 : 0,
                    maxBarThickness: 46
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: chartType === 'pie' } },
                scales: chartType === 'bar' ? {
                    y: { beginAtZero: true, ticks: { callback: function (v) { return v.toLocaleString('vi-VN'); } } }
                } : {}
            }
        });
    })();
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>