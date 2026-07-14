<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reports &amp; Statistics - Admin FlowerShop</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

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
            --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
        }

        body {
            font-family: 'Inter', sans-serif;
            background-color: var(--secondary-bg);
            color: var(--text-main);
            overflow-x: hidden;
        }

        .sidebar {
            background-color: var(--sidebar-bg);
            min-height: 100vh;
            color: #ffffff;
            transition: all 0.3s;
            z-index: 100;
        }

        .sidebar .brand {
            padding: 1.5rem 1rem;
            font-size: 1.25rem;
            font-weight: 700;
            letter-spacing: 0.05em;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
            color: #38bdf8;
        }

        .sidebar-menu {
            list-style: none;
            padding: 1rem 0;
            margin: 0;
        }

        .sidebar-menu li a {
            display: flex;
            align-items: center;
            padding: 0.75rem 1.5rem;
            color: #cbd5e1;
            text-decoration: none;
            transition: all 0.2s;
            border-left: 4px solid transparent;
        }

        .sidebar-menu li a:hover,
        .sidebar-menu li.active a {
            background-color: var(--sidebar-hover);
            color: #ffffff;
            border-left-color: var(--primary-color);
        }

        .sidebar-menu li a i {
            margin-right: 0.75rem;
            font-size: 1.1rem;
            width: 20px;
            text-align: center;
        }

        .main-content {
            padding: 2rem;
            min-height: 100vh;
        }

        .header-container {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
        }

        .page-title {
            font-size: 1.75rem;
            font-weight: 700;
            color: var(--text-main);
            margin: 0;
        }

        .user-nav-profile {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            padding: 0.5rem 1rem;
            border-radius: 8px;
            background: #ffffff;
            border: 1px solid var(--border-color);
        }

        .card {
            background: #ffffff;
            border: 1px solid var(--border-color);
            border-radius: 12px;
            box-shadow: var(--shadow-sm);
            overflow: hidden;
            margin-bottom: 1.5rem;
        }

        .card-header-custom {
            padding: 1.25rem 1.5rem;
            background-color: #ffffff;
            border-bottom: 1px solid var(--border-color);
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 1rem;
        }

        .table {
            margin-bottom: 0;
        }

        .table th {
            background-color: #f8fafc;
            color: var(--text-muted);
            font-weight: 600;
            text-transform: uppercase;
            font-size: 0.75rem;
            letter-spacing: 0.05em;
            padding: 1rem 1.5rem;
            border-bottom: 1px solid var(--border-color);
        }

        .table td {
            padding: 1rem 1.5rem;
            vertical-align: middle;
            font-size: 0.875rem;
            border-bottom: 1px solid var(--border-color);
        }

        .table tbody tr:hover {
            background-color: #f8fafc;
        }

        .report-tabs {
            display: flex;
            flex-wrap: wrap;
            gap: 0.5rem;
            margin-bottom: 1.5rem;
        }

        .report-tabs a {
            display: inline-flex;
            align-items: center;
            gap: 0.4rem;
            padding: 0.55rem 1rem;
            border-radius: 9999px;
            text-decoration: none;
            font-size: 0.875rem;
            font-weight: 500;
            color: var(--text-muted);
            background: #ffffff;
            border: 1px solid var(--border-color);
            transition: all 0.2s;
        }

        .report-tabs a:hover {
            border-color: var(--primary-color);
            color: var(--primary-color);
        }

        .report-tabs a.active {
            background: var(--primary-color);
            border-color: var(--primary-color);
            color: #ffffff;
        }

        .metric-mini {
            border: 1px solid var(--border-color);
            border-radius: 12px;
            background: #ffffff;
            box-shadow: var(--shadow-sm);
            padding: 1rem 1.25rem;
            height: 100%;
        }

        .metric-mini .label {
            font-size: 0.75rem;
            font-weight: 500;
            color: var(--text-muted);
            text-transform: uppercase;
            letter-spacing: 0.04em;
            margin-bottom: 0.25rem;
        }

        .metric-mini .value {
            font-size: 1.35rem;
            font-weight: 700;
            color: var(--text-main);
        }

        .rank-badge {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 28px;
            height: 28px;
            border-radius: 9999px;
            font-size: 0.75rem;
            font-weight: 600;
            background: #e0e7ff;
            color: #3730a3;
        }

        .rank-badge.top1 { background: #fef3c7; color: #b45309; }
        .rank-badge.top2 { background: #e5e7eb; color: #374151; }
        .rank-badge.top3 { background: #ffedd5; color: #c2410c; }

        .status-badge {
            font-weight: 500;
            padding: 0.35em 0.75em;
            border-radius: 9999px;
            font-size: 0.75rem;
            display: inline-block;
        }

        .status-completed { background: #dcfce7; color: #15803d; }
        .status-pending { background: #fef3c7; color: #b45309; }
        .status-processing { background: #dbeafe; color: #1d4ed8; }
        .status-shipping { background: #e0e7ff; color: #3730a3; }
        .status-cancelled { background: #fee2e2; color: #b91c1c; }
        .status-default { background: #f3f4f6; color: #4b5563; }

        .level-out { background: #fee2e2; color: #b91c1c; }
        .level-low { background: #fef3c7; color: #b45309; }
        .level-ok { background: #dcfce7; color: #15803d; }

        .badge-active {
            background-color: #dcfce7;
            color: #15803d;
            font-weight: 500;
            padding: 0.35em 0.65em;
            border-radius: 9999px;
            font-size: 0.75rem;
        }

        .badge-inactive {
            background-color: #fee2e2;
            color: #b91c1c;
            font-weight: 500;
            padding: 0.35em 0.65em;
            border-radius: 9999px;
            font-size: 0.75rem;
        }

        .filter-form {
            display: flex;
            flex-wrap: wrap;
            gap: 0.75rem;
            align-items: center;
        }

        .filter-form .form-control,
        .filter-form .form-select {
            border-radius: 8px;
            border: 1px solid var(--border-color);
            min-width: 120px;
        }

        .filter-form .form-control:focus,
        .filter-form .form-select:focus {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.2);
        }

        .btn-primary-custom {
            background-color: var(--primary-color);
            color: #ffffff;
            border: none;
            font-weight: 500;
            border-radius: 8px;
            padding: 0.45rem 1.1rem;
            transition: all 0.2s;
        }

        .btn-primary-custom:hover {
            background-color: var(--primary-hover);
            color: #ffffff;
        }

        .empty-state {
            text-align: center;
            padding: 3rem 1.5rem;
            color: var(--text-muted);
        }
    </style>
</head>
<body>

<div class="container-fluid p-0">
    <div class="row g-0">
        <!-- Sidebar -->
        <div class="col-md-3 col-lg-2 sidebar">
            <div class="brand">
                <i class="fa-solid fa-seedling me-2"></i> FlowerShop Admin
            </div>
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

        <!-- Main Content -->
        <div class="col-md-9 col-lg-10 main-content">
            <div class="header-container">
                <div>
                    <h1 class="page-title">Reports &amp; Statistics</h1>
                    <p class="text-muted mb-0">Báo cáo doanh thu, tồn kho và khách hàng</p>
                </div>
                <div class="user-nav-profile">
                    <i class="fa-solid fa-circle-user text-primary fs-4"></i>
                    <div>
                        <span class="d-block fw-semibold" style="font-size: 0.85rem;">Administrator</span>
                        <span class="text-muted d-block" style="font-size: 0.75rem;">admin@flowershop.com</span>
                    </div>
                </div>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-warning alert-dismissible fade show border-0 shadow-sm" role="alert">
                    <i class="fa-solid fa-triangle-exclamation me-2"></i>
                    ${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <!-- Summary metrics -->
            <div class="row g-3 mb-3">
                <div class="col-sm-6 col-lg-4">
                    <div class="metric-mini">
                        <div class="label">Total Revenue (Completed)</div>
                        <div class="value">${totalRevenueDisplay} <span style="font-size: 0.85rem; font-weight: 500;">₫</span></div>
                    </div>
                </div>
                <c:if test="${reportType == 'revenue'}">
                    <div class="col-sm-6 col-lg-4">
                        <div class="metric-mini">
                            <div class="label">Revenue Year ${year}</div>
                            <div class="value">${yearRevenueDisplay} <span style="font-size: 0.85rem; font-weight: 500;">₫</span></div>
                        </div>
                    </div>
                    <div class="col-sm-6 col-lg-4">
                        <div class="metric-mini">
                            <div class="label">Completed Orders ${year}</div>
                            <div class="value">${yearOrderCount}</div>
                        </div>
                    </div>
                </c:if>
            </div>

            <!-- Report type tabs -->
            <div class="report-tabs">
                <a href="${pageContext.request.contextPath}/admin/reports?type=revenue"
                   class="${reportType == 'revenue' ? 'active' : ''}">
                    <i class="fa-solid fa-chart-column"></i> Revenue
                </a>
                <a href="${pageContext.request.contextPath}/admin/reports?type=topselling&limit=${limit}"
                   class="${reportType == 'topselling' ? 'active' : ''}">
                    <i class="fa-solid fa-trophy"></i> Top Selling
                </a>
                <a href="${pageContext.request.contextPath}/admin/reports?type=inventory&threshold=${threshold}"
                   class="${reportType == 'inventory' ? 'active' : ''}">
                    <i class="fa-solid fa-boxes-stacked"></i> Inventory
                </a>
                <a href="${pageContext.request.contextPath}/admin/reports?type=customer&limit=${limit}"
                   class="${reportType == 'customer' ? 'active' : ''}">
                    <i class="fa-solid fa-star"></i> Top Customers
                </a>
                <a href="${pageContext.request.contextPath}/admin/reports?type=orderstatus"
                   class="${reportType == 'orderstatus' ? 'active' : ''}">
                    <i class="fa-solid fa-chart-pie"></i> Order Status
                </a>
            </div>

            <!-- ===================== REVENUE ===================== -->
            <c:if test="${reportType == 'revenue'}">
                <div class="card">
                    <div class="card-header-custom">
                        <h5 class="mb-0 fw-semibold">
                            <i class="fa-solid fa-chart-column text-primary me-2"></i>
                            Revenue by Month — ${year}
                        </h5>
                        <form class="filter-form" method="get" action="${pageContext.request.contextPath}/admin/reports">
                            <input type="hidden" name="type" value="revenue"/>
                            <label class="small text-muted mb-0">Year</label>
                            <input type="number" name="year" class="form-control form-control-sm"
                                   value="${year}" min="2000" max="${currentYear + 1}" style="width: 110px;"/>
                            <button type="submit" class="btn btn-primary-custom btn-sm">
                                <i class="fa-solid fa-filter me-1"></i> Apply
                            </button>
                        </form>
                    </div>
                    <div class="table-responsive">
                        <table class="table align-middle">
                            <thead>
                                <tr>
                                    <th>Month</th>
                                    <th class="text-end">Order Count</th>
                                    <th class="text-end">Revenue</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty reportData}">
                                        <c:forEach var="row" items="${reportData}">
                                            <tr>
                                                <td class="fw-semibold">
                                                    Tháng ${row.month}/${row.year}
                                                </td>
                                                <td class="text-end">${row.orderCount}</td>
                                                <td class="text-end">${row.revenueDisplay} ₫</td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="3">
                                                <div class="empty-state">
                                                    <i class="fa-regular fa-folder-open fs-1 d-block mb-3"></i>
                                                    <span>Chưa có doanh thu hoàn tất trong năm ${year}</span>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>

            <!-- ===================== TOP SELLING ===================== -->
            <c:if test="${reportType == 'topselling'}">
                <div class="card">
                    <div class="card-header-custom">
                        <h5 class="mb-0 fw-semibold">
                            <i class="fa-solid fa-trophy text-warning me-2"></i>
                            Top Selling Flowers
                        </h5>
                        <form class="filter-form" method="get" action="${pageContext.request.contextPath}/admin/reports">
                            <input type="hidden" name="type" value="topselling"/>
                            <label class="small text-muted mb-0">Top N</label>
                            <input type="number" name="limit" class="form-control form-control-sm"
                                   value="${limit}" min="1" max="100" style="width: 90px;"/>
                            <button type="submit" class="btn btn-primary-custom btn-sm">
                                <i class="fa-solid fa-filter me-1"></i> Apply
                            </button>
                        </form>
                    </div>
                    <div class="table-responsive">
                        <table class="table align-middle">
                            <thead>
                                <tr>
                                    <th style="width: 70px;">Rank</th>
                                    <th>Flower</th>
                                    <th class="text-end">Qty Sold</th>
                                    <th class="text-end">Revenue</th>
                                    <th class="text-end">Stock</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty reportData}">
                                        <c:forEach var="row" items="${reportData}" varStatus="st">
                                            <tr>
                                                <td>
                                                    <span class="rank-badge ${st.index == 0 ? 'top1' : (st.index == 1 ? 'top2' : (st.index == 2 ? 'top3' : ''))}">
                                                        ${st.index + 1}
                                                    </span>
                                                </td>
                                                <td>
                                                    <span class="fw-semibold text-dark">${row.flowerName}</span>
                                                    <span class="text-muted small d-block">ID #${row.flowerId}</span>
                                                </td>
                                                <td class="text-end fw-medium">${row.totalSold}</td>
                                                <td class="text-end">${row.revenueDisplay} ₫</td>
                                                <td class="text-end">
                                                    <c:choose>
                                                        <c:when test="${row.stockQuantity <= 0}">
                                                            <span class="status-badge status-cancelled">Hết hàng</span>
                                                        </c:when>
                                                        <c:when test="${row.stockQuantity <= 10}">
                                                            <span class="status-badge status-pending">${row.stockQuantity}</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="text-muted">${row.stockQuantity}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="5">
                                                <div class="empty-state">
                                                    <i class="fa-regular fa-folder-open fs-1 d-block mb-3"></i>
                                                    <span>Chưa có dữ liệu bán hàng</span>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>

            <!-- ===================== INVENTORY ===================== -->
            <c:if test="${reportType == 'inventory'}">
                <div class="card">
                    <div class="card-header-custom">
                        <h5 class="mb-0 fw-semibold">
                            <i class="fa-solid fa-boxes-stacked text-success me-2"></i>
                            Inventory Report
                        </h5>
                        <form class="filter-form" method="get" action="${pageContext.request.contextPath}/admin/reports">
                            <input type="hidden" name="type" value="inventory"/>
                            <label class="small text-muted mb-0">Low stock ≤</label>
                            <input type="number" name="threshold" class="form-control form-control-sm"
                                   value="${threshold}" min="1" max="1000" style="width: 90px;"/>
                            <button type="submit" class="btn btn-primary-custom btn-sm">
                                <i class="fa-solid fa-filter me-1"></i> Apply
                            </button>
                        </form>
                    </div>
                    <div class="table-responsive">
                        <table class="table align-middle">
                            <thead>
                                <tr>
                                    <th>Flower</th>
                                    <th class="text-end">Stock</th>
                                    <th>Level</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty reportData}">
                                        <c:forEach var="row" items="${reportData}">
                                            <tr>
                                                <td>
                                                    <span class="fw-semibold text-dark">${row.flowerName}</span>
                                                    <span class="text-muted small d-block">ID #${row.flowerId}</span>
                                                </td>
                                                <td class="text-end fw-medium">${row.stockQuantity}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${row.stockLevel == 'OUT'}">
                                                            <span class="status-badge level-out">OUT</span>
                                                        </c:when>
                                                        <c:when test="${row.stockLevel == 'LOW'}">
                                                            <span class="status-badge level-low">LOW</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="status-badge level-ok">OK</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${row.status}">
                                                            <span class="badge-active">Active</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge-inactive">Inactive</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="4">
                                                <div class="empty-state">
                                                    <i class="fa-regular fa-folder-open fs-1 d-block mb-3"></i>
                                                    <span>Chưa có dữ liệu tồn kho</span>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>

            <!-- ===================== TOP CUSTOMERS ===================== -->
            <c:if test="${reportType == 'customer'}">
                <div class="card">
                    <div class="card-header-custom">
                        <h5 class="mb-0 fw-semibold">
                            <i class="fa-solid fa-star text-warning me-2"></i>
                            Top Customers (VIP)
                        </h5>
                        <form class="filter-form" method="get" action="${pageContext.request.contextPath}/admin/reports">
                            <input type="hidden" name="type" value="customer"/>
                            <label class="small text-muted mb-0">Top N</label>
                            <input type="number" name="limit" class="form-control form-control-sm"
                                   value="${limit}" min="1" max="100" style="width: 90px;"/>
                            <button type="submit" class="btn btn-primary-custom btn-sm">
                                <i class="fa-solid fa-filter me-1"></i> Apply
                            </button>
                        </form>
                    </div>
                    <div class="table-responsive">
                        <table class="table align-middle">
                            <thead>
                                <tr>
                                    <th style="width: 70px;">#</th>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th class="text-end">Orders</th>
                                    <th class="text-end">Total Spent</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty reportData}">
                                        <c:forEach var="row" items="${reportData}" varStatus="st">
                                            <tr>
                                                <td>
                                                    <span class="rank-badge ${st.index == 0 ? 'top1' : (st.index == 1 ? 'top2' : (st.index == 2 ? 'top3' : ''))}">
                                                        ${st.index + 1}
                                                    </span>
                                                </td>
                                                <td class="fw-semibold">${row.fullName}</td>
                                                <td class="text-muted">${row.email}</td>
                                                <td class="text-end">${row.totalOrders}</td>
                                                <td class="text-end">${row.totalSpentDisplay} ₫</td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="5">
                                                <div class="empty-state">
                                                    <i class="fa-regular fa-folder-open fs-1 d-block mb-3"></i>
                                                    <span>Chưa có dữ liệu khách hàng VIP</span>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>

            <!-- ===================== ORDER STATUS ===================== -->
            <c:if test="${reportType == 'orderstatus'}">
                <div class="card">
                    <div class="card-header-custom">
                        <h5 class="mb-0 fw-semibold">
                            <i class="fa-solid fa-chart-pie text-primary me-2"></i>
                            Orders by Status
                        </h5>
                    </div>
                    <div class="table-responsive">
                        <table class="table align-middle">
                            <thead>
                                <tr>
                                    <th>Status</th>
                                    <th class="text-end">Order Count</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty reportData}">
                                        <c:forEach var="row" items="${reportData}">
                                            <tr>
                                                <td>
                                                    <c:set var="stName" value="${row.status}"/>
                                                    <c:choose>
                                                        <c:when test="${stName == 'Completed'}">
                                                            <span class="status-badge status-completed">${stName}</span>
                                                        </c:when>
                                                        <c:when test="${stName == 'Pending'}">
                                                            <span class="status-badge status-pending">${stName}</span>
                                                        </c:when>
                                                        <c:when test="${stName == 'Processing'}">
                                                            <span class="status-badge status-processing">${stName}</span>
                                                        </c:when>
                                                        <c:when test="${stName == 'Shipping'}">
                                                            <span class="status-badge status-shipping">${stName}</span>
                                                        </c:when>
                                                        <c:when test="${stName == 'Cancelled'}">
                                                            <span class="status-badge status-cancelled">${stName}</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="status-badge status-default">${stName}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="text-end fw-semibold">${row.orderCount}</td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="2">
                                                <div class="empty-state">
                                                    <i class="fa-regular fa-folder-open fs-1 d-block mb-3"></i>
                                                    <span>Chưa có đơn hàng</span>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>

        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
