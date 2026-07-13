<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order Management - Admin Dashboard</title>
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

        .card {
            background: #ffffff;
            border: 1px solid var(--border-color);
            border-radius: 12px;
            box-shadow: var(--shadow-sm);
            overflow: hidden;
            margin-bottom: 2rem;
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

        .status-badge {
            font-weight: 500;
            padding: 0.35em 0.75em;
            border-radius: 9999px;
            font-size: 0.75rem;
            display: inline-block;
        }

        .status-Completed { background: #dcfce7; color: #15803d; }
        .status-Pending { background: #fef3c7; color: #b45309; }
        .status-Confirmed { background: #dbeafe; color: #1d4ed8; }
        .status-Shipping { background: #e0e7ff; color: #3730a3; }
        .status-Cancelled { background: #fee2e2; color: #b91c1c; }

        .btn-action {
            width: 32px;
            height: 32px;
            padding: 0;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            border-radius: 6px;
            transition: all 0.2s;
            margin-right: 0.25rem;
        }

        .search-wrapper {
            position: relative;
            min-width: 240px;
        }

        .search-wrapper i {
            position: absolute;
            left: 1rem;
            top: 50%;
            transform: translateY(-50%);
            color: var(--text-muted);
        }

        .search-wrapper input {
            padding-left: 2.5rem;
            border-radius: 8px;
            border: 1px solid var(--border-color);
        }

        .filter-select {
            border-radius: 8px;
            border: 1px solid var(--border-color);
            min-width: 160px;
        }

        .filter-select:focus,
        .search-wrapper input:focus {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.2);
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

        .filter-form {
            display: flex;
            flex-wrap: wrap;
            gap: 0.75rem;
            align-items: center;
            flex: 1;
        }

        .pagination .page-link {
            color: var(--text-main);
            border: 1px solid var(--border-color);
            padding: 0.5rem 0.75rem;
            margin: 0 2px;
            border-radius: 6px;
        }

        .pagination .page-item.active .page-link {
            background-color: var(--primary-color);
            border-color: var(--primary-color);
            color: #ffffff;
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
                <li class="active"><a href="${pageContext.request.contextPath}/admin/orders"><i class="fa-solid fa-cart-shopping"></i> Orders</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/reports"><i class="fa-solid fa-file-invoice-dollar"></i> Reports</a></li>
                <li class="mt-5"><a href="${pageContext.request.contextPath}/logout" class="text-danger"><i class="fa-solid fa-right-from-bracket text-danger"></i> Logout</a></li>
            </ul>
        </div>

        <!-- Main Content -->
        <div class="col-md-9 col-lg-10 main-content">
            <div class="header-container">
                <div>
                    <h1 class="page-title">Order Management</h1>
                    <p class="text-muted mb-0">Track and update customer orders</p>
                </div>
                <div class="user-nav-profile">
                    <i class="fa-solid fa-circle-user text-primary fs-4"></i>
                    <div>
                        <span class="d-block fw-semibold" style="font-size: 0.85rem;">Administrator</span>
                        <span class="text-muted d-block" style="font-size: 0.75rem;">admin@flowershop.com</span>
                    </div>
                </div>
            </div>

            <!-- Main Card -->
            <div class="card">
                <div class="card-header-custom">
                    <form action="${pageContext.request.contextPath}/admin/orders" method="get" class="filter-form">
                        <input type="hidden" name="action" value="list">
                        <div class="search-wrapper">
                            <i class="fa-solid fa-magnifying-glass"></i>
                            <input type="text" name="keyword" class="form-control"
                                   placeholder="Search by receiver name, phone or order ID..." value="${keyword}">
                        </div>
                        <select name="status" class="form-select filter-select">
                            <option value="" ${empty status ? 'selected' : ''}>All Status</option>
                            <c:forEach var="s" items="${['Pending','Confirmed','Shipping','Completed','Cancelled']}">
                                <option value="${s}" ${status == s ? 'selected' : ''}>${s}</option>
                            </c:forEach>
                        </select>
                        <button type="submit" class="btn btn-outline-primary" style="border-radius: 8px;">
                            <i class="fa-solid fa-filter me-1"></i> Search
                        </button>
                        <c:if test="${not empty keyword or not empty status}">
                            <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-outline-secondary" style="border-radius: 8px;">
                                Clear
                            </a>
                        </c:if>
                    </form>
                </div>

                <div class="table-responsive">
                    <table class="table align-middle">
                        <thead>
                            <tr>
                                <th style="width: 70px;">#</th>
                                <th>Receiver</th>
                                <th>Phone</th>
                                <th>Order Date</th>
                                <th class="text-end">Total</th>
                                <th style="width: 130px;">Status</th>
                                <th style="width: 160px; text-align: center;">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty orderList}">
                                    <c:forEach var="o" items="${orderList}">
                                        <tr>
                                            <td><span class="text-muted fw-medium">#${o.orderID}</span></td>
                                            <td><span class="fw-semibold text-dark">${o.receiverName}</span></td>
                                            <td><span class="text-muted">${o.receiverPhone}</span></td>
                                            <td><fmt:formatDate value="${o.orderDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                                            <td class="text-end">
                                                <fmt:formatNumber value="${o.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                            </td>
                                            <td>
                                                <span class="status-badge status-${o.status}">${o.status}</span>
                                            </td>
                                            <td class="text-center">
                                                <a href="${pageContext.request.contextPath}/admin/orders?action=detail&id=${o.orderID}"
                                                   class="btn btn-outline-primary btn-action" title="View Detail">
                                                    <i class="fa-solid fa-eye"></i>
                                                </a>
                                                <button type="button" class="btn btn-outline-danger btn-action"
                                                        onclick="confirmDelete(${o.orderID})" title="Delete Order">
                                                    <i class="fa-solid fa-trash-can"></i>
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="7" class="text-center py-5">
                                            <i class="fa-regular fa-folder-open text-muted fs-1 d-block mb-3"></i>
                                            <span class="text-muted">No orders found matching your query.</span>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

                <div class="card-footer bg-white border-top py-3 d-flex justify-content-between align-items-center flex-wrap gap-2">
                    <span class="text-muted small">
                        Total: <strong>${totalRecords}</strong> order(s)
                        &middot; Page <strong>${currentPage}</strong> / <strong>${totalPages}</strong>
                    </span>
                    <c:if test="${totalPages > 1}">
                        <nav aria-label="Page navigation">
                            <ul class="pagination justify-content-center mb-0">
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link"
                                       href="${pageContext.request.contextPath}/admin/orders?action=list&page=${currentPage - 1}&keyword=${keyword}&status=${status}">
                                        <span aria-hidden="true">&laquo;</span>
                                    </a>
                                </li>
                                <c:forEach var="i" begin="1" end="${totalPages}">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link"
                                           href="${pageContext.request.contextPath}/admin/orders?action=list&page=${i}&keyword=${keyword}&status=${status}">${i}</a>
                                    </li>
                                </c:forEach>
                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link"
                                       href="${pageContext.request.contextPath}/admin/orders?action=list&page=${currentPage + 1}&keyword=${keyword}&status=${status}">
                                        <span aria-hidden="true">&raquo;</span>
                                    </a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Modal confirm delete -->
<div class="modal fade" id="deleteModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg" style="border-radius: 12px;">
            <div class="modal-header border-bottom-0">
                <h5 class="modal-title fw-bold text-danger">
                    <i class="fa-solid fa-circle-exclamation me-1"></i> Confirm Delete Order
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body text-dark">
                Are you sure you want to delete this order? This action cannot be undone,
                and may fail if the order already has payment or review records linked to it.
            </div>
            <div class="modal-footer border-top-0">
                <button type="button" class="btn btn-light" data-bs-dismiss="modal" style="border-radius: 8px;">Cancel</button>
                <form id="deleteForm" method="post" action="${pageContext.request.contextPath}/admin/orders">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="orderID" id="deleteOrderId">
                    <button type="submit" class="btn btn-danger" style="border-radius: 8px;">Delete</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function confirmDelete(orderId) {
        document.getElementById('deleteOrderId').value = orderId;
        var myModal = new bootstrap.Modal(document.getElementById('deleteModal'));
        myModal.show();
    }
</script>
</body>
</html>