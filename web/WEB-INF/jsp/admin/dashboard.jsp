<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Admin FlowerShop</title>
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
        }

        .metric-card {
            border: 1px solid var(--border-color);
            border-radius: 12px;
            background: #ffffff;
            box-shadow: var(--shadow-sm);
            padding: 1.25rem 1.5rem;
            height: 100%;
            transition: box-shadow 0.2s;
        }

        .metric-card:hover {
            box-shadow: var(--shadow-md);
        }

        .metric-icon {
            width: 48px;
            height: 48px;
            border-radius: 12px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-size: 1.25rem;
            flex-shrink: 0;
        }

        .metric-icon.users { background: #e0e7ff; color: #4338ca; }
        .metric-icon.flowers { background: #dcfce7; color: #15803d; }
        .metric-icon.orders { background: #fef3c7; color: #b45309; }
        .metric-icon.revenue { background: #fce7f3; color: #be185d; }

        .metric-label {
            font-size: 0.8rem;
            font-weight: 500;
            color: var(--text-muted);
            text-transform: uppercase;
            letter-spacing: 0.04em;
            margin-bottom: 0.25rem;
        }

        .metric-value {
            font-size: 1.75rem;
            font-weight: 700;
            color: var(--text-main);
            line-height: 1.2;
        }

        .metric-sub {
            font-size: 0.8rem;
            color: var(--text-muted);
            margin-top: 0.25rem;
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
                <li class="active"><a href="${pageContext.request.contextPath}/admin/dashboard"><i class="fa-solid fa-chart-line"></i> Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/users"><i class="fa-solid fa-users"></i> Users</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/categories"><i class="fa-solid fa-tags"></i> Categories</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/flowers"><i class="fa-solid fa-leaf"></i> Flowers</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/orders"><i class="fa-solid fa-cart-shopping"></i> Orders</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/reports"><i class="fa-solid fa-file-invoice-dollar"></i> Reports</a></li>
                <li class="mt-5"><a href="${pageContext.request.contextPath}/logout" class="text-danger"><i class="fa-solid fa-right-from-bracket text-danger"></i> Logout</a></li>
            </ul>
        </div>

        <!-- Main Content -->
        <div class="col-md-9 col-lg-10 main-content">
            <div class="header-container">
                <div>
                    <h1 class="page-title">Dashboard</h1>
                    <p class="text-muted mb-0">Tổng quan hoạt động cửa hàng hoa</p>
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

            <!-- Metric cards -->
            <div class="row g-3 mb-4">
                <div class="col-sm-6 col-xl-3">
                    <div class="metric-card d-flex align-items-start gap-3">
                        <div class="metric-icon users">
                            <i class="fa-solid fa-users"></i>
                        </div>
                        <div>
                            <div class="metric-label">Total Users</div>
                            <div class="metric-value">${totalUsers}</div>
                            <div class="metric-sub">
                                <i class="fa-solid fa-circle-check text-success me-1"></i>
                                ${activeUsers} đang hoạt động
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6 col-xl-3">
                    <div class="metric-card d-flex align-items-start gap-3">
                        <div class="metric-icon flowers">
                            <i class="fa-solid fa-leaf"></i>
                        </div>
                        <div>
                            <div class="metric-label">Total Flowers</div>
                            <div class="metric-value">${totalFlowers}</div>
                            <div class="metric-sub">
                                <i class="fa-solid fa-seedling text-success me-1"></i>
                                ${activeFlowers} đang bán
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6 col-xl-3">
                    <div class="metric-card d-flex align-items-start gap-3">
                        <div class="metric-icon orders">
                            <i class="fa-solid fa-cart-shopping"></i>
                        </div>
                        <div>
                            <div class="metric-label">Total Orders</div>
                            <div class="metric-value">${totalOrders}</div>
                            <div class="metric-sub">Tất cả trạng thái</div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6 col-xl-3">
                    <div class="metric-card d-flex align-items-start gap-3">
                        <div class="metric-icon revenue">
                            <i class="fa-solid fa-coins"></i>
                        </div>
                        <div>
                            <div class="metric-label">Total Revenue</div>
                            <div class="metric-value" style="font-size: 1.35rem;">
                                ${totalRevenueDisplay}
                                <span style="font-size: 0.85rem; font-weight: 500;">₫</span>
                            </div>
                            <div class="metric-sub">Đơn Completed</div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row g-3">
                <!-- Top Selling Flowers -->
                <div class="col-lg-8">
                    <div class="card">
                        <div class="card-header-custom">
                            <h5 class="mb-0 fw-semibold">
                                <i class="fa-solid fa-trophy text-warning me-2"></i>
                                Top Selling Flowers
                            </h5>
                            <span class="text-muted small">Top 5 (đơn Completed)</span>
                        </div>
                        <div class="table-responsive">
                            <table class="table align-middle">
                                <thead>
                                    <tr>
                                        <th style="width: 70px;">#</th>
                                        <th>Tên hoa</th>
                                        <th class="text-end">Đã bán</th>
                                        <th class="text-end">Doanh thu</th>
                                        <th class="text-end">Tồn kho</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${not empty topFlowers}">
                                            <c:forEach var="f" items="${topFlowers}" varStatus="st">
                                                <tr>
                                                    <td>
                                                        <span class="rank-badge ${st.index == 0 ? 'top1' : (st.index == 1 ? 'top2' : (st.index == 2 ? 'top3' : ''))}">
                                                            ${st.index + 1}
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <span class="fw-semibold text-dark">${f.flowerName}</span>
                                                        <span class="text-muted small d-block">ID #${f.flowerId}</span>
                                                    </td>
                                                    <td class="text-end fw-medium">${f.totalSold}</td>
                                                    <td class="text-end">
                                                        ${f.revenueDisplay} ₫
                                                    </td>
                                                    <td class="text-end">
                                                        <c:choose>
                                                            <c:when test="${f.stockQuantity <= 0}">
                                                                <span class="status-badge status-cancelled">Hết hàng</span>
                                                            </c:when>
                                                            <c:when test="${f.stockQuantity <= 10}">
                                                                <span class="status-badge status-pending">${f.stockQuantity}</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="text-muted">${f.stockQuantity}</span>
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
                </div>

                <!-- Order status + Top customers -->
                <div class="col-lg-4">
                    <div class="card">
                        <div class="card-header-custom">
                            <h5 class="mb-0 fw-semibold">
                                <i class="fa-solid fa-chart-pie text-primary me-2"></i>
                                Order Status
                            </h5>
                        </div>
                        <div class="p-3">
                            <c:choose>
                                <c:when test="${not empty orderStatusSummary}">
                                    <ul class="list-group list-group-flush">
                                        <c:forEach var="s" items="${orderStatusSummary}">
                                            <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                                <c:set var="stName" value="${s.status}"/>
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
                                                <span class="fw-semibold">${s.orderCount}</span>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </c:when>
                                <c:otherwise>
                                    <div class="empty-state py-4">
                                        <span class="small">Chưa có đơn hàng</span>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <div class="card">
                        <div class="card-header-custom">
                            <h5 class="mb-0 fw-semibold">
                                <i class="fa-solid fa-star text-warning me-2"></i>
                                Top Customers
                            </h5>
                        </div>
                        <div class="table-responsive">
                            <table class="table align-middle">
                                <thead>
                                    <tr>
                                        <th>Khách hàng</th>
                                        <th class="text-end">Đơn</th>
                                        <th class="text-end">Chi tiêu</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${not empty topCustomers}">
                                            <c:forEach var="cust" items="${topCustomers}">
                                                <tr>
                                                    <td>
                                                        <span class="fw-semibold d-block">${cust.fullName}</span>
                                                        <span class="text-muted small">${cust.email}</span>
                                                    </td>
                                                    <td class="text-end">${cust.totalOrders}</td>
                                                    <td class="text-end">
                                                        ${cust.totalSpentDisplay} ₫
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr>
                                                <td colspan="3">
                                                    <div class="empty-state py-4">
                                                        <span class="small">Chưa có dữ liệu khách hàng VIP</span>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
