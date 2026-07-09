<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="col-md-3 col-lg-2 d-md-block bg-light sidebar collapse show">

    <div class="position-sticky pt-3">

        <h5 class="text-center mb-4 fw-bold text-success">
            <i class="bi bi-shield-lock-fill me-2"></i>
            Admin Panel
        </h5>

        <ul class="nav flex-column">

            <li class="nav-item">
                <a class="nav-link"
                   href="${pageContext.request.contextPath}/admin/dashboard">
                    <i class="bi bi-speedometer2 me-2"></i>
                    Tổng quan
                </a>
            </li>

            <li class="nav-item">
                <a class="nav-link"
                   href="${pageContext.request.contextPath}/admin/users">
                    <i class="bi bi-people-fill me-2"></i>
                    Quản lý người dùng
                </a>
            </li>

            <li class="nav-item">
                <a class="nav-link"
                   href="${pageContext.request.contextPath}/admin/categories">
                    <i class="bi bi-tags-fill me-2"></i>
                    Quản lý danh mục
                </a>
            </li>

            <li class="nav-item">
                <a class="nav-link"
                   href="${pageContext.request.contextPath}/admin/flowers">
                    <i class="bi bi-flower1 me-2"></i>
                    Quản lý hoa
                </a>
            </li>

            <li class="nav-item">
                <a class="nav-link"
                   href="${pageContext.request.contextPath}/admin/orders">
                    <i class="bi bi-receipt-cutoff me-2"></i>
                    Quản lý đơn hàng
                </a>
            </li>

            <li class="nav-item">
                <a class="nav-link"
                   href="${pageContext.request.contextPath}/admin/reports">
                    <i class="bi bi-bar-chart-line-fill me-2"></i>
                    Báo cáo thống kê
                </a>
            </li>

        </ul>

        <hr>

        <ul class="nav flex-column">

            <li class="nav-item">
                <a class="nav-link text-primary"
                   href="${pageContext.request.contextPath}/home">
                    <i class="bi bi-house-door-fill me-2"></i>
                    Về trang chủ
                </a>
            </li>

            <li class="nav-item">
                <a class="nav-link text-danger"
                   href="${pageContext.request.contextPath}/logout">
                    <i class="bi bi-box-arrow-right me-2"></i>
                    Đăng xuất
                </a>
            </li>

        </ul>

    </div>

</div>