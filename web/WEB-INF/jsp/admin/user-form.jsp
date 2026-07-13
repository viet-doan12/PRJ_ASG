<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${formTitle} - Admin Dashboard</title>
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
            max-width: 800px;
            margin: 0 auto 2rem auto;
        }

        .card-header-custom {
            padding: 1.25rem 1.5rem;
            background-color: #ffffff;
            border-bottom: 1px solid var(--border-color);
            font-weight: 600;
            font-size: 1.1rem;
        }

        .form-label {
            font-weight: 500;
            font-size: 0.875rem;
            color: var(--text-main);
            margin-bottom: 0.5rem;
        }

        .form-control:focus, .form-select:focus, .form-check-input:focus {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.2);
        }

        .btn-save {
            background-color: var(--primary-color);
            color: #ffffff;
            border: none;
            font-weight: 500;
            border-radius: 8px;
            padding: 0.6rem 1.5rem;
            transition: all 0.2s;
        }

        .btn-save:hover {
            background-color: var(--primary-hover);
            color: #ffffff;
        }

        .btn-cancel {
            background-color: #f3f4f6;
            color: var(--text-main);
            border: 1px solid var(--border-color);
            font-weight: 500;
            border-radius: 8px;
            padding: 0.6rem 1.5rem;
            transition: all 0.2s;
            text-decoration: none;
            display: inline-block;
        }

        .btn-cancel:hover {
            background-color: #e5e7eb;
            color: var(--text-main);
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
                    <h1 class="page-title">${formTitle}</h1>
                    <p class="text-muted mb-0">Fill in the fields to create or update a user account</p>
                </div>
                <div class="user-nav-profile">
                    <i class="fa-solid fa-circle-user text-primary fs-4"></i>
                    <div>
                        <span class="d-block fw-semibold" style="font-size: 0.85rem;">Administrator</span>
                        <span class="text-muted d-block" style="font-size: 0.75rem;">admin@flowershop.com</span>
                    </div>
                </div>
            </div>

            <!-- Error Alerts -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show border-0 shadow-sm mx-auto mb-4" role="alert" style="max-width: 800px;">
                    <i class="fa-solid fa-circle-xmark me-2"></i> ${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <!-- Card Form -->
            <div class="card">
                <div class="card-header-custom">
                    <i class="fa-solid fa-user text-indigo me-2"></i> User Details
                </div>
                <div class="card-body p-4">
                    <form action="${pageContext.request.contextPath}/admin/users" method="post" id="userForm" class="needs-validation" novalidate>
                        <input type="hidden" name="action" value="${action}">

                        <c:if test="${action == 'update'}">
                            <input type="hidden" name="userID" value="${user.userID}">
                            <div class="mb-3">
                                <label class="form-label text-muted">User ID</label>
                                <input type="text" class="form-control bg-light" value="#${user.userID}" readonly disabled>
                            </div>
                        </c:if>

                        <!-- Full Name -->
                        <div class="mb-3">
                            <label for="fullName" class="form-label">Full Name <span class="text-danger">*</span></label>
                            <input type="text"
                                   class="form-control"
                                   id="fullName"
                                   name="fullName"
                                   placeholder="Enter full name"
                                   value="${user.fullName}"
                                   required
                                   maxlength="100">
                            <div class="invalid-feedback">Please provide a full name.</div>
                        </div>

                        <!-- Email -->
                        <div class="mb-3">
                            <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
                            <input type="email"
                                   class="form-control"
                                   id="email"
                                   name="email"
                                   placeholder="user@example.com"
                                   value="${user.email}"
                                   required>
                            <div class="invalid-feedback">Please provide a valid email.</div>
                        </div>

                        <!-- Phone -->
                        <div class="mb-3">
                            <label for="phone" class="form-label">Phone</label>
                            <input type="text"
                                   class="form-control"
                                   id="phone"
                                   name="phone"
                                   placeholder="0xxxxxxxxx (optional)"
                                   value="${user.phone}"
                                   maxlength="20">
                            <div class="form-text">Vietnamese phone: 10 digits starting with 0. Leave blank if unknown.</div>
                        </div>

                        <!-- Address -->
                        <div class="mb-3">
                            <label for="address" class="form-label">Address</label>
                            <textarea class="form-control"
                                      id="address"
                                      name="address"
                                      rows="2"
                                      placeholder="Shipping / contact address (optional)">${user.address}</textarea>
                        </div>

                        <!-- Role -->
                        <div class="mb-3">
                            <label for="roleID" class="form-label">Role <span class="text-danger">*</span></label>
                            <select class="form-select" id="roleID" name="roleID" required>
                                <option value="">-- Select role --</option>
                                <c:forEach var="role" items="${roleList}">
                                    <option value="${role.roleID}" ${user.roleID == role.roleID ? 'selected' : ''}>
                                        ${role.roleName}
                                    </option>
                                </c:forEach>
                            </select>
                            <div class="invalid-feedback">Please select a role.</div>
                        </div>

                        <!-- Password -->
                        <div class="mb-3">
                            <label for="password" class="form-label">
                                Password
                                <c:if test="${action == 'insert'}"><span class="text-danger">*</span></c:if>
                            </label>
                            <input type="password"
                                   class="form-control"
                                   id="password"
                                   name="password"
                                   placeholder="${action == 'insert' ? 'Min 6 chars, letters + numbers' : 'Leave blank to keep current password'}"
                                   ${action == 'insert' ? 'required' : ''}
                                   autocomplete="new-password">
                            <c:if test="${action == 'update'}">
                                <div class="form-text">Leave blank to keep the existing password. Provide a new one to change it.</div>
                            </c:if>
                            <c:if test="${action == 'insert'}">
                                <div class="form-text">At least 6 characters, must include letters and numbers.</div>
                            </c:if>
                        </div>

                        <!-- Status Switch -->
                        <div class="mb-4">
                            <label class="form-label d-block">Status</label>
                            <div class="form-check form-switch p-0 ps-5 mt-2">
                                <input class="form-check-input fs-5"
                                       type="checkbox"
                                       id="status"
                                       name="status"
                                       style="cursor: pointer;"
                                       ${user == null || user.status ? 'checked' : ''}>
                                <label class="form-check-label ms-2 text-muted" for="status" style="cursor: pointer;">
                                    Active (user can log in)
                                </label>
                            </div>
                        </div>

                        <!-- Form Buttons -->
                        <div class="d-flex justify-content-end gap-2 pt-3 border-top border-light">
                            <a href="${pageContext.request.contextPath}/admin/users?action=list" class="btn-cancel">
                                Cancel
                            </a>
                            <button type="submit" class="btn-save btn btn-primary">
                                <i class="fa-solid fa-floppy-disk me-1"></i> Save User
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
    (function () {
        'use strict'
        var forms = document.querySelectorAll('.needs-validation')
        Array.prototype.slice.call(forms)
            .forEach(function (form) {
                form.addEventListener('submit', function (event) {
                    if (!form.checkValidity()) {
                        event.preventDefault()
                        event.stopPropagation()
                    }
                    form.classList.add('was-validated')
                }, false)
            })
    })()
</script>
</body>
</html>
