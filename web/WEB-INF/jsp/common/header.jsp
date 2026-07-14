<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Flower Shop</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
          rel="stylesheet">

    <!-- CSS -->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

    <!-- Sticky footer: đẩy footer xuống đáy khi nội dung ngắn -->
    <style>
        html, body {
            height: 100%;
        }

        body {
            display: flex;
            flex-direction: column;
            min-height: 100vh;
        }

        .main-content {
            flex: 1 0 auto;
        }

        footer {
            flex-shrink: 0;
        }

        .navbar-search {
            max-width: 360px;
            width: 100%;
        }

        .navbar-search input[type="search"] {
            border-radius: 20px 0 0 20px;
        }

        .navbar-search button {
            border-radius: 0 20px 20px 0;
        }
    </style>
</head>

<body>

<nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm sticky-top">

    <div class="container">

        <a class="navbar-brand fw-bold text-success"
           href="${pageContext.request.contextPath}/home">

            🌸 Flower Shop

        </a>

        <button class="navbar-toggler"
                type="button"
                data-bs-toggle="collapse"
                data-bs-target="#mainNav">

            <span class="navbar-toggler-icon"></span>

        </button>

        <div class="collapse navbar-collapse"
             id="mainNav">

            <ul class="navbar-nav me-auto">

                <li class="nav-item">
                    <a class="nav-link"
                       href="${pageContext.request.contextPath}/home">
                        Trang chủ
                    </a>
                </li>

                <li class="nav-item">
                    <a class="nav-link"
                       href="${pageContext.request.contextPath}/flowers">
                        Hoa
                    </a>
                </li>

                <li class="nav-item">
                    <a class="nav-link"
                       href="${pageContext.request.contextPath}/cart">
                        Giỏ hàng
                    </a>
                </li>

            </ul>

            <!-- THANH TÌM KIẾM -->
            <form class="d-flex navbar-search mx-auto my-2 my-lg-0"
                  action="${pageContext.request.contextPath}/flowers"
                  method="get"
                  role="search">
                <input type="search"
                       name="keyword"
                       class="form-control"
                       placeholder="Tìm kiếm hoa..."
                       aria-label="Tìm kiếm hoa"
                       value="${param.keyword}">
                <button class="btn btn-success" type="submit">
                    <i class="bi bi-search"></i>
                </button>
            </form>

            <ul class="navbar-nav">

                <c:choose>

                    <c:when test="${not empty sessionScope.user}">

                        <c:if test="${sessionScope.user.roleID == 1}">
                            <li class="nav-item">
                                <a class="nav-link"
                                   href="${pageContext.request.contextPath}/admin/dashboard">
                                    Quản trị
                                </a>
                            </li>
                        </c:if>

                        <c:if test="${sessionScope.user.roleID == 2}">
                            <li class="nav-item">
                                <a class="nav-link"
                                   href="${pageContext.request.contextPath}/staff/orders">
                                    Quản lý đơn hàng
                                </a>
                            </li>
                        </c:if>

                        <li class="nav-item dropdown">

                            <a class="nav-link dropdown-toggle"
                               href="#"
                               role="button"
                               data-bs-toggle="dropdown">

                                Xin chào,
                                <strong>${sessionScope.user.fullName}</strong>

                            </a>

                            <ul class="dropdown-menu dropdown-menu-end">

                                <li>
                                    <a class="dropdown-item"
                                       href="${pageContext.request.contextPath}/profile">
                                        Hồ sơ cá nhân
                                    </a>
                                </li>

                                <c:if test="${sessionScope.user.roleID == 3}">
                                    <li>
                                        <a class="dropdown-item"
                                           href="${pageContext.request.contextPath}/orders">
                                            Đơn hàng của tôi
                                        </a>
                                    </li>
                                </c:if>

                                <li>
                                    <hr class="dropdown-divider">
                                </li>

                                <li>
                                    <a class="dropdown-item text-danger"
                                       href="${pageContext.request.contextPath}/logout">
                                        Đăng xuất
                                    </a>
                                </li>

                            </ul>

                        </li>

                    </c:when>

                    <c:otherwise>

                        <li class="nav-item">
                            <a class="nav-link"
                               href="${pageContext.request.contextPath}/login">
                                Đăng nhập
                            </a>
                        </li>

                        <li class="nav-item">
                            <a class="nav-link"
                               href="${pageContext.request.contextPath}/register">
                                Đăng ký
                            </a>
                        </li>

                    </c:otherwise>

                </c:choose>

            </ul>

        </div>

    </div>

</nav>

<!-- Hiển thị Message chung -->
<jsp:include page="/WEB-INF/jsp/common/message.jsp"/>

<!-- Bọc toàn bộ nội dung chính của từng trang -->
<div class="main-content">