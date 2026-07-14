<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order #${order.orderID} - Admin Dashboard</title>
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
            margin-bottom: 1.5rem;
        }

        .page-title {
            font-size: 1.5rem;
            font-weight: 700;
            color: var(--text-main);
            margin: 0;
        }

        .card {
            background: #ffffff;
            border: 1px solid var(--border-color);
            border-radius: 12px;
            box-shadow: var(--shadow-sm);
            margin-bottom: 1.5rem;
        }

        .card-header-custom {
            padding: 1.1rem 1.5rem;
            background-color: #ffffff;
            border-bottom: 1px solid var(--border-color);
            font-weight: 600;
            color: var(--primary-color);
        }

        .card-body-custom {
            padding: 1.5rem;
        }

        .info-row {
            margin-bottom: 0.6rem;
            font-size: 0.9rem;
        }

        .info-row .label {
            color: var(--text-muted);
            display: inline-block;
            min-width: 130px;
        }

        .status-badge {
            font-size: 13px;
            padding: 0.4em 1em;
            border-radius: 9999px;
            font-weight: 600;
        }

        .status-Pending { background: #fef3c7; color: #b45309; }
        .status-Confirmed { background: #dbeafe; color: #1d4ed8; }
        .status-Shipping { background: #e0e7ff; color: #3730a3; }
        .status-Completed { background: #dcfce7; color: #15803d; }
        .status-Cancelled { background: #fee2e2; color: #b91c1c; }

        .table th {
            background-color: #f8fafc;
            color: var(--text-muted);
            font-weight: 600;
            text-transform: uppercase;
            font-size: 0.75rem;
            letter-spacing: 0.05em;
        }

        .item-img {
            width: 56px;
            height: 56px;
            object-fit: cover;
            border-radius: 8px;
            border: 1px solid var(--border-color);
            background: #f3f4f6;
        }

        .grand-total {
            font-size: 1.1rem;
            font-weight: 700;
            color: var(--primary-color);
        }

        .btn-save-status {
            background-color: var(--primary-color);
            color: #fff;
            border: none;
            font-weight: 500;
            border-radius: 8px;
            padding: 0.5rem 1.25rem;
        }

        .btn-save-status:hover {
            background-color: var(--primary-hover);
            color: #fff;
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

            <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-sm btn-outline-secondary mb-3">
                <i class="fa-solid fa-arrow-left me-1"></i> Back to Order List
            </a>

            <c:if test="${empty order}">
                <div class="alert alert-warning border-0 shadow-sm">
                    <i class="fa-solid fa-triangle-exclamation me-2"></i> Order not found.
                </div>
            </c:if>

            <c:if test="${not empty order}">
                <div class="header-container">
                    <h1 class="page-title">Order #${order.orderID}</h1>
                    <span class="status-badge status-${order.status}">${order.status}</span>
                </div>

                <div class="row">
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-header-custom">
                                <i class="fa-solid fa-truck me-2"></i> Shipping Information
                            </div>
                            <div class="card-body-custom">
                                <div class="info-row"><span class="label">Receiver:</span> ${order.receiverName}</div>
                                <div class="info-row"><span class="label">Phone:</span> ${order.receiverPhone}</div>
                                <div class="info-row"><span class="label">Address:</span> ${order.shippingAddress}</div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-header-custom">
                                <i class="fa-solid fa-circle-info me-2"></i> Order Information
                            </div>
                            <div class="card-body-custom">
                                <div class="info-row">
                                    <span class="label">Order Date:</span>
                                    <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/>
                                </div>
                                <div class="info-row"><span class="label">Customer ID:</span> #${order.userID}</div>
                                <div class="info-row">
                                    <span class="label">Total Amount:</span>
                                    <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Update status + delete -->
                <div class="card">
                    <div class="card-body-custom d-flex flex-wrap gap-3 align-items-end justify-content-between">
                        <form method="post" action="${pageContext.request.contextPath}/admin/orders" class="d-flex align-items-end gap-2 m-0">
                            <input type="hidden" name="action" value="updateStatus">
                            <input type="hidden" name="orderID" value="${order.orderID}">
                            <div>
                                <label class="form-label mb-1 fw-semibold">Update Status</label>
                                <select name="status" class="form-select">
                                    <c:forEach var="s" items="${['Pending','Confirmed','Shipping','Completed','Cancelled']}">
                                        <option value="${s}" ${order.status == s ? 'selected' : ''}>${s}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <button type="submit" class="btn btn-save-status">
                                <i class="fa-solid fa-check me-1"></i> Save
                            </button>
                        </form>

                        <form method="post" action="${pageContext.request.contextPath}/admin/orders" class="m-0"
                              onsubmit="return confirm('Delete order #${order.orderID}? This cannot be undone.');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="orderID" value="${order.orderID}">
                            <button type="submit" class="btn btn-outline-danger">
                                <i class="fa-solid fa-trash-can me-1"></i> Delete Order
                            </button>
                        </form>
                    </div>
                </div>

                <!-- Line items -->
                <div class="card">
                    <div class="card-header-custom">
                        <i class="fa-solid fa-list me-2"></i> Order Items
                    </div>
                    <div class="table-responsive">
                        <table class="table align-middle mb-0">
                            <thead>
                                <tr>
                                    <th>Product</th>
                                    <th class="text-center">Unit Price</th>
                                    <th class="text-center">Quantity</th>
                                    <th class="text-end">Subtotal</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty detailList}">
                                        <tr>
                                            <td colspan="4" class="text-center text-muted py-4">No items in this order.</td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="d" items="${detailList}">
                                            <c:set var="flowerKey" value="flowerName_${d.flowerID}"/>
                                            <c:set var="imageKey" value="flowerImage_${d.flowerID}"/>
                                            <tr>
                                                <td>
                                                    <div class="d-flex align-items-center gap-2">
                                                        <c:choose>
                                                            <c:when test="${not empty requestScope[imageKey]}">
                                                                <img src="${pageContext.request.contextPath}/images/flowers/${requestScope[imageKey]}" class="item-img">
                                                            </c:when>
                                                            <c:otherwise>
                                                                <img src="${pageContext.request.contextPath}/images/no-image.jpg" class="item-img">
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <span>${requestScope[flowerKey]}</span>
                                                    </div>
                                                </td>
                                                <td class="text-center">
                                                    <fmt:formatNumber value="${d.unitPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                </td>
                                                <td class="text-center">${d.quantity}</td>
                                                <td class="text-end fw-semibold">
                                                    <fmt:formatNumber value="${d.unitPrice * d.quantity}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                            <tfoot>
                                <tr>
                                    <td colspan="3" class="text-end fw-bold">Grand Total</td>
                                    <td class="text-end grand-total">
                                        <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                    </td>
                                </tr>
                            </tfoot>
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