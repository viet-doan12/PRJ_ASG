<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý đơn hàng - Staff</title>
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
                margin-bottom: 2rem;
                flex-wrap: wrap;
                gap: 1rem;
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

            .status-Completed {
                background: #dcfce7;
                color: #15803d;
            }
            .status-Pending {
                background: #fef3c7;
                color: #b45309;
            }
            .status-Confirmed {
                background: #dbeafe;
                color: #1d4ed8;
            }
            .status-Shipping {
                background: #e0e7ff;
                color: #3730a3;
            }
            .status-Cancelled {
                background: #fee2e2;
                color: #b91c1c;
            }

            .btn-action {
                width: 32px;
                height: 32px;
                padding: 0;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                border-radius: 6px;
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

            .filter-select:focus, .search-wrapper input:focus {
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

            .detail-label {
                font-size: 0.75rem;
                text-transform: uppercase;
                letter-spacing: 0.05em;
                color: var(--text-muted);
                font-weight: 600;
                margin-bottom: 0.25rem;
            }

            .detail-value {
                font-size: 0.95rem;
                color: var(--text-main);
                font-weight: 500;
            }

            .flower-thumb {
                width: 48px;
                height: 48px;
                object-fit: cover;
                border-radius: 8px;
                border: 1px solid var(--border-color);
            }

            .btn-prepare {
                background-color: #16a34a;
                border-color: #16a34a;
                color: #fff;
            }
            .btn-prepare:hover {
                background-color: #15803d;
                border-color: #15803d;
                color: #fff;
            }
        </style>
    </head>
    <body>

        <div class="container-fluid p-0">
            <div class="row g-0">
                <!-- Sidebar -->
                <div class="col-md-3 col-lg-2 sidebar">
                    <div class="brand">
                        <i class="fa-solid fa-seedling me-2"></i> FlowerShop Staff
                    </div>
                    <ul class="sidebar-menu">
                        <li class="active"><a href="${pageContext.request.contextPath}/staff/orders"><i class="fa-solid fa-cart-shopping"></i> Đơn hàng</a></li>
                        <li><a href="${pageContext.request.contextPath}/logout" class="text-danger"><i class="fa-solid fa-right-from-bracket text-danger"></i> Đăng xuất</a></li>
                    </ul>
                </div>

                <!-- Main Content -->
                <div class="col-md-9 col-lg-10 main-content">
                    <div class="header-container">
                        <div>
                            <h1 class="page-title">
                                <c:choose>
                                    <c:when test="${not empty order}">Chi tiết đơn hàng #${order.orderID}</c:when>
                                    <c:otherwise>Quản lý đơn hàng</c:otherwise>
                                </c:choose>
                            </h1>
                            <p class="text-muted mb-0">
                                <c:choose>
                                    <c:when test="${not empty order}">Xem thông tin, đổi trạng thái và chuẩn bị hàng</c:when>
                                    <c:otherwise>Xem đơn hàng, đổi trạng thái và chuẩn bị hàng cho khách</c:otherwise>
                                </c:choose>
                            </p>
                        </div>
                        <div class="user-nav-profile">
                            <i class="fa-solid fa-circle-user text-primary fs-4"></i>
                            <div>
                                <span class="d-block fw-semibold" style="font-size: 0.85rem;">${sessionScope.user.fullName}</span>
                                <span class="text-muted d-block" style="font-size: 0.75rem;">${sessionScope.user.email}</span>
                            </div>
                        </div>
                    </div>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger">${errorMessage}</div>
                    </c:if>

                    <c:choose>
                        <%-- ========================================================= --%>
                        <%-- CHẾ ĐỘ CHI TIẾT ĐƠN HÀNG                                  --%>
                        <%-- ========================================================= --%>
                        <c:when test="${not empty order}">

                            <div class="mb-3">
                                <a href="${pageContext.request.contextPath}/staff/orders?action=list" class="btn btn-outline-secondary btn-sm">
                                    <i class="fa-solid fa-arrow-left me-1"></i> Quay lại danh sách
                                </a>
                            </div>

                            <div class="card">
                                <div class="card-header-custom">
                                    <h5 class="mb-0 fw-bold">Thông tin đơn hàng</h5>
                                    <span class="status-badge status-${order.status}" style="font-size: 0.85rem;">${order.status}</span>
                                </div>
                                <div class="p-4">
                                    <div class="row g-4">
                                        <div class="col-md-4">
                                            <div class="detail-label">Người nhận</div>
                                            <div class="detail-value">${order.receiverName}</div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="detail-label">Số điện thoại</div>
                                            <div class="detail-value">${order.receiverPhone}</div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="detail-label">Ngày đặt</div>
                                            <div class="detail-value"><fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/></div>
                                        </div>
                                        <div class="col-md-8">
                                            <div class="detail-label">Địa chỉ giao hàng</div>
                                            <div class="detail-value">${order.shippingAddress}</div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="detail-label">Tổng tiền</div>
                                            <div class="detail-value text-primary fw-bold">
                                                <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="card">
                                <div class="card-header-custom">
                                    <h5 class="mb-0 fw-bold">Sản phẩm trong đơn</h5>
                                </div>
                                <div class="table-responsive">
                                    <table class="table align-middle">
                                        <thead>
                                            <tr>
                                                <th>Sản phẩm</th>
                                                <th class="text-end">Đơn giá</th>
                                                <th class="text-center">Số lượng</th>
                                                <th class="text-end">Thành tiền</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="d" items="${detailList}">
                                                <tr>
                                                    <td>
                                                        <div class="d-flex align-items-center gap-2">
                                                            <c:if test="${not empty requestScope['flowerImage_'.concat(d.flowerID)]}">
                                                                <img src="${pageContext.request.contextPath}/image/${requestScope['flowerImage_'.concat(d.flowerID)]}"
                                                                     class="flower-thumb" alt="">
                                                            </c:if>
                                                            <span class="fw-semibold">${requestScope['flowerName_'.concat(d.flowerID)]}</span>
                                                        </div>
                                                    </td>
                                                    <td class="text-end"><fmt:formatNumber value="${d.unitPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                                                    <td class="text-center">${d.quantity}</td>
                                                    <td class="text-end fw-semibold">
                                                        <fmt:formatNumber value="${d.unitPrice * d.quantity}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                            <c:if test="${empty detailList}">
                                                <tr><td colspan="4" class="text-center text-muted py-4">Đơn hàng không có sản phẩm nào.</td></tr>
                                            </c:if>
                                        </tbody>
                                    </table>
                                </div>
                            </div>

                            <div class="card">
                                <div class="card-header-custom">
                                    <h5 class="mb-0 fw-bold">Xử lý đơn hàng</h5>
                                </div>
                                <div class="p-4">
                                    <div class="d-flex flex-wrap gap-3 align-items-end">

                                        <%-- Nút "Chuẩn bị hàng": chỉ hiện khi đơn đang Pending --%>
                                        <c:if test="${order.status == 'Pending'}">
                                            <form method="post" action="${pageContext.request.contextPath}/staff/orders">
                                                <input type="hidden" name="action" value="prepare">
                                                <input type="hidden" name="orderID" value="${order.orderID}">
                                                <button type="submit" class="btn btn-prepare">
                                                    <i class="fa-solid fa-box me-1"></i> Chuẩn bị hàng
                                                </button>
                                            </form>
                                        </c:if>

                                        <%-- Đổi trạng thái tự do (khi đơn chưa Completed/Cancelled) --%>
                                        <c:if test="${order.status != 'Completed' && order.status != 'Cancelled'}">
                                            <form method="post" action="${pageContext.request.contextPath}/staff/orders" class="d-flex gap-2 align-items-end">
                                                <input type="hidden" name="action" value="updateStatus">
                                                <input type="hidden" name="orderID" value="${order.orderID}">
                                                <div>
                                                    <label class="detail-label d-block">Đổi trạng thái</label>
                                                    <select name="status" class="form-select filter-select">
                                                        <c:forEach var="s" items="${allowedStatusList}">
                                                            <option value="${s}" ${order.status == s ? 'selected' : ''}>${s}</option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                                <button type="submit" class="btn btn-primary">
                                                    <i class="fa-solid fa-rotate me-1"></i> Cập nhật
                                                </button>
                                            </form>
                                        </c:if>

                                        <c:if test="${order.status == 'Completed' || order.status == 'Cancelled'}">
                                            <span class="text-muted">Đơn hàng đã <strong>${order.status}</strong>, không thể chỉnh sửa thêm.</span>
                                        </c:if>
                                    </div>
                                </div>
                            </div>

                        </c:when>

                        <%-- ========================================================= --%>
                        <%-- CHẾ ĐỘ DANH SÁCH ĐƠN HÀNG                                 --%>
                        <%-- ========================================================= --%>
                        <c:otherwise>

                            <div class="card">
                                <div class="card-header-custom">
                                    <form action="${pageContext.request.contextPath}/staff/orders" method="get" class="filter-form">
                                        <input type="hidden" name="action" value="list">
                                        <div class="search-wrapper">
                                            <i class="fa-solid fa-magnifying-glass"></i>
                                            <input type="text" name="keyword" class="form-control"
                                                   placeholder="Tìm theo tên, SĐT hoặc mã đơn..." value="${keyword}">
                                        </div>
                                        <select name="status" class="form-select filter-select">
                                            <option value="" ${empty status ? 'selected' : ''}>Tất cả trạng thái</option>
                                                <option value="">Tất cả</option>
                                                <option value="Pending" ${status=='Pending'?'selected':''}>Pending</option>
                                                <option value="Confirmed" ${status=='Confirmed'?'selected':''}>Confirmed</option>
                                                <option value="Shipping" ${status=='Shipping'?'selected':''}>Shipping</option>
                                                <option value="Completed" ${status=='Completed'?'selected':''}>Completed</option>
                                                <option value="Cancelled" ${status=='Cancelled'?'selected':''}>Cancelled</option>
                                            
                                        </select>
                                        <button type="submit" class="btn btn-outline-primary" style="border-radius: 8px;">
                                            <i class="fa-solid fa-filter me-1"></i> Tìm kiếm
                                        </button>
                                        <c:if test="${not empty keyword or not empty status}">
                                            <a href="${pageContext.request.contextPath}/staff/orders" class="btn btn-outline-secondary" style="border-radius: 8px;">
                                                Xóa lọc
                                            </a>
                                        </c:if>
                                    </form>
                                </div>

                                <div class="table-responsive">
                                    <table class="table align-middle">
                                        <thead>
                                            <tr>
                                                <th style="width: 70px;">#</th>
                                                <th>Người nhận</th>
                                                <th>Điện thoại</th>
                                                <th>Ngày đặt</th>
                                                <th class="text-end">Tổng tiền</th>
                                                <th style="width: 130px;">Trạng thái</th>
                                                <th style="width: 160px; text-align: center;">Thao tác</th>
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
                                                                <a href="${pageContext.request.contextPath}/staff/orders?action=detail&id=${o.orderID}"
                                                                   class="btn btn-outline-primary btn-action" title="Xem chi tiết">
                                                                    <i class="fa-solid fa-eye"></i>
                                                                </a>
                                                                <c:if test="${o.status == 'Pending'}">
                                                                    <form method="post" action="${pageContext.request.contextPath}/staff/orders" class="d-inline">
                                                                        <input type="hidden" name="action" value="prepare">
                                                                        <input type="hidden" name="orderID" value="${o.orderID}">
                                                                        <button type="submit" class="btn btn-prepare btn-action" title="Chuẩn bị hàng">
                                                                            <i class="fa-solid fa-box"></i>
                                                                        </button>
                                                                    </form>
                                                                </c:if>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:when>
                                                <c:otherwise>
                                                    <tr>
                                                        <td colspan="7" class="text-center py-5">
                                                            <i class="fa-regular fa-folder-open text-muted fs-1 d-block mb-3"></i>
                                                            <span class="text-muted">Không tìm thấy đơn hàng nào.</span>
                                                        </td>
                                                    </tr>
                                                </c:otherwise>
                                            </c:choose>
                                        </tbody>
                                    </table>
                                </div>

                                <div class="card-footer bg-white border-top py-3 d-flex justify-content-between align-items-center flex-wrap gap-2">
                                    <span class="text-muted small">
                                        Tổng: <strong>${totalRecords}</strong> đơn hàng
                                        &middot; Trang <strong>${currentPage}</strong> / <strong>${totalPages}</strong>
                                    </span>
                                    <c:if test="${totalPages > 1}">
                                        <nav aria-label="Page navigation">
                                            <ul class="pagination justify-content-center mb-0">
                                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                                    <a class="page-link"
                                                       href="${pageContext.request.contextPath}/staff/orders?action=list&page=${currentPage - 1}&keyword=${keyword}&status=${status}">
                                                        <span aria-hidden="true">&laquo;</span>
                                                    </a>
                                                </li>
                                                <c:forEach var="i" begin="1" end="${totalPages}">
                                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                        <a class="page-link"
                                                           href="${pageContext.request.contextPath}/staff/orders?action=list&page=${i}&keyword=${keyword}&status=${status}">${i}</a>
                                                    </li>
                                                </c:forEach>
                                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                                    <a class="page-link"
                                                       href="${pageContext.request.contextPath}/staff/orders?action=list&page=${currentPage + 1}&keyword=${keyword}&status=${status}">
                                                        <span aria-hidden="true">&raquo;</span>
                                                    </a>
                                                </li>
                                            </ul>
                                        </nav>
                                    </c:if>
                                </div>
                            </div>

                        </c:otherwise>
                    </c:choose>

                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>