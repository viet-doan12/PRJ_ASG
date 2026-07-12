<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management - Admin Dashboard</title>
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
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
            --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
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

        .badge-role {
            background-color: #e0e7ff;
            color: #3730a3;
            font-weight: 500;
            padding: 0.35em 0.65em;
            border-radius: 9999px;
            font-size: 0.75rem;
        }

        .btn-primary-custom {
            background-color: var(--primary-color);
            color: #ffffff;
            border: none;
            font-weight: 500;
            border-radius: 8px;
            padding: 0.5rem 1.25rem;
            transition: all 0.2s;
            box-shadow: var(--shadow-sm);
        }

        .btn-primary-custom:hover {
            background-color: var(--primary-hover);
            color: #ffffff;
        }

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

        .pagination .page-item.disabled .page-link {
            color: var(--text-muted);
            background-color: #f3f4f6;
        }

        .search-wrapper {
            position: relative;
            min-width: 220px;
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

        .search-wrapper input:focus,
        .filter-select:focus {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.2);
        }

        .filter-select {
            border-radius: 8px;
            border: 1px solid var(--border-color);
            min-width: 140px;
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
                <li class="active"><a href="${pageContext.request.contextPath}/admin/users"><i class="fa-solid fa-users"></i> Users</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/categories"><i class="fa-solid fa-tags"></i> Categories</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/flowers"><i class="fa-solid fa-leaf"></i> Flowers</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/orders"><i class="fa-solid fa-cart-shopping"></i> Orders</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/reports"><i class="fa-solid fa-file-invoice-dollar"></i> Reports</a></li>
                <li class="mt-5"><a href="${pageContext.request.contextPath}/logout" class="text-danger"><i class="fa-solid fa-right-from-bracket text-danger"></i> Logout</a></li>
            </ul>
        </div>

        <!-- Main Content Area -->
        <div class="col-md-9 col-lg-10 main-content">
            <!-- Header section -->
            <div class="header-container">
                <div>
                    <h1 class="page-title">User Management</h1>
                    <p class="text-muted mb-0">Manage system users, roles and account status</p>
                </div>
                <div class="user-nav-profile">
                    <i class="fa-solid fa-circle-user text-primary fs-4"></i>
                    <div>
                        <span class="d-block fw-semibold" style="font-size: 0.85rem;">Administrator</span>
                        <span class="text-muted d-block" style="font-size: 0.75rem;">admin@flowershop.com</span>
                    </div>
                </div>
            </div>

            <!-- Toast Messages -->
            <c:if test="${not empty sessionScope.toastMessage}">
                <div class="alert alert-${sessionScope.toastType} alert-dismissible fade show border-0 shadow-sm" role="alert">
                    <c:choose>
                        <c:when test="${sessionScope.toastType == 'success'}">
                            <i class="fa-solid fa-circle-check me-2"></i>
                        </c:when>
                        <c:otherwise>
                            <i class="fa-solid fa-triangle-exclamation me-2"></i>
                        </c:otherwise>
                    </c:choose>
                    ${sessionScope.toastMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <%
                    session.removeAttribute("toastMessage");
                    session.removeAttribute("toastType");
                %>
            </c:if>

            <!-- Main Card -->
            <div class="card">
                <div class="card-header-custom">
                    <!-- Search + filter form -->
                    <form action="${pageContext.request.contextPath}/admin/users" method="get" class="filter-form">
                        <input type="hidden" name="action" value="list">
                        <div class="search-wrapper">
                            <i class="fa-solid fa-magnifying-glass"></i>
                            <input type="text" name="search" class="form-control" placeholder="Search name, email, phone..." value="${search}">
                        </div>
                        <select name="roleId" class="form-select filter-select">
                            <option value="">All Roles</option>
                            <c:forEach var="role" items="${roleList}">
                                <option value="${role.roleID}" ${roleId != null && roleId == role.roleID ? 'selected' : ''}>
                                    ${role.roleName}
                                </option>
                            </c:forEach>
                        </select>
                        <select name="status" class="form-select filter-select">
                            <option value="" ${empty status ? 'selected' : ''}>All Status</option>
                            <option value="true" ${status == 'true' ? 'selected' : ''}>Active</option>
                            <option value="false" ${status == 'false' ? 'selected' : ''}>Locked</option>
                        </select>
                        <button type="submit" class="btn btn-outline-primary" style="border-radius: 8px;">
                            <i class="fa-solid fa-filter me-1"></i> Search
                        </button>
                    </form>
                    <!-- Add button -->
                    <a href="${pageContext.request.contextPath}/admin/users?action=add" class="btn btn-primary-custom text-nowrap">
                        <i class="fa-solid fa-plus me-1"></i> Add User
                    </a>
                </div>

                <div class="table-responsive">
                    <table class="table align-middle">
                        <thead>
                            <tr>
                                <th style="width: 70px;">ID</th>
                                <th>Full Name</th>
                                <th>Email</th>
                                <th>Phone</th>
                                <th>Role</th>
                                <th style="width: 120px;">Status</th>
                                <th style="width: 140px;">Created</th>
                                <th style="width: 160px; text-align: center;">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty userList}">
                                    <c:forEach var="u" items="${userList}">
                                        <tr>
                                            <td><span class="text-muted fw-medium">#${u.userID}</span></td>
                                            <td>
                                                <span class="fw-semibold text-dark">${u.fullName}</span>
                                            </td>
                                            <td>
                                                <span class="text-muted">${u.email}</span>
                                            </td>
                                            <td>
                                                <span class="text-muted">${not empty u.phone ? u.phone : '-'}</span>
                                            </td>
                                            <td>
                                                <c:set var="roleName" value="Unknown"/>
                                                <c:forEach var="role" items="${roleList}">
                                                    <c:if test="${role.roleID == u.roleID}">
                                                        <c:set var="roleName" value="${role.roleName}"/>
                                                    </c:if>
                                                </c:forEach>
                                                <span class="badge-role">${roleName}</span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${u.status}">
                                                        <span class="badge-active"><i class="fa-solid fa-circle-dot me-1"></i> Active</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge-inactive"><i class="fa-solid fa-lock me-1"></i> Locked</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${u.createdDate != null}">
                                                        <fmt:formatDate value="${u.createdDate}" pattern="dd/MM/yyyy"/>
                                                    </c:when>
                                                    <c:otherwise>-</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="text-center">
                                                <a href="${pageContext.request.contextPath}/admin/users?action=edit&id=${u.userID}"
                                                   class="btn btn-outline-primary btn-action"
                                                   title="Edit User">
                                                    <i class="fa-solid fa-pen-to-square"></i>
                                                </a>
                                                <c:choose>
                                                    <c:when test="${u.status}">
                                                        <a href="${pageContext.request.contextPath}/admin/users?action=lock&id=${u.userID}"
                                                           class="btn btn-outline-warning btn-action"
                                                           title="Lock User"
                                                           onclick="return confirm('Lock user ${u.fullName}?');">
                                                            <i class="fa-solid fa-lock"></i>
                                                        </a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a href="${pageContext.request.contextPath}/admin/users?action=unlock&id=${u.userID}"
                                                           class="btn btn-outline-success btn-action"
                                                           title="Unlock User"
                                                           onclick="return confirm('Unlock user ${u.fullName}?');">
                                                            <i class="fa-solid fa-lock-open"></i>
                                                        </a>
                                                    </c:otherwise>
                                                </c:choose>
                                                <button type="button"
                                                        class="btn btn-outline-danger btn-action"
                                                        onclick="confirmDelete(${u.userID}, '${u.fullName}')"
                                                        title="Lock / Soft Delete">
                                                    <i class="fa-solid fa-trash-can"></i>
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="8" class="text-center py-5">
                                            <i class="fa-regular fa-folder-open text-muted fs-1 d-block mb-3"></i>
                                            <span class="text-muted">No users found matching your query.</span>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

                <!-- Card Footer / Pagination -->
                <div class="card-footer bg-white border-top py-3 d-flex justify-content-between align-items-center flex-wrap gap-2">
                    <span class="text-muted small">
                        Total: <strong>${totalRecords}</strong> user(s)
                        &middot; Page <strong>${currentPage}</strong> / <strong>${totalPages}</strong>
                    </span>
                    <c:if test="${totalPages > 1}">
                        <nav aria-label="Page navigation">
                            <ul class="pagination justify-content-center mb-0">
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link"
                                       href="${pageContext.request.contextPath}/admin/users?action=list&page=${currentPage - 1}&search=${search}&roleId=${roleId}&status=${status}"
                                       aria-label="Previous">
                                        <span aria-hidden="true">&laquo;</span>
                                    </a>
                                </li>
                                <c:forEach var="i" begin="1" end="${totalPages}">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link"
                                           href="${pageContext.request.contextPath}/admin/users?action=list&page=${i}&search=${search}&roleId=${roleId}&status=${status}">${i}</a>
                                    </li>
                                </c:forEach>
                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link"
                                       href="${pageContext.request.contextPath}/admin/users?action=list&page=${currentPage + 1}&search=${search}&roleId=${roleId}&status=${status}"
                                       aria-label="Next">
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

<!-- Modal confirm soft-delete (lock) -->
<div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg" style="border-radius: 12px;">
            <div class="modal-header border-bottom-0">
                <h5 class="modal-title fw-bold text-danger" id="deleteModalLabel">
                    <i class="fa-solid fa-circle-exclamation me-1"></i> Confirm Lock User
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body text-dark">
                Are you sure you want to lock user "<strong id="deleteUserName"></strong>"?
                This is a soft-delete (status = Locked). The account will not be permanently removed.
            </div>
            <div class="modal-footer border-top-0">
                <button type="button" class="btn btn-light" data-bs-dismiss="modal" style="border-radius: 8px;">Cancel</button>
                <a id="confirmDeleteBtn" href="#" class="btn btn-danger" style="border-radius: 8px;">Lock User</a>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function confirmDelete(id, name) {
        document.getElementById('deleteUserName').innerText = name;
        var deleteUrl = '${pageContext.request.contextPath}/admin/users?action=delete&id=' + id;
        document.getElementById('confirmDeleteBtn').setAttribute('href', deleteUrl);
        var myModal = new bootstrap.Modal(document.getElementById('deleteModal'));
        myModal.show();
    }
</script>
</body>
</html>
