<%-- 
    Document   : homepage
    Author     : Group 6
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<style>
    .category-nav-wrapper {
        background-color: #ffffff;
        position: sticky;
        top: 56px;
        z-index: 1020;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
        border-bottom: 1px solid #ebebeb;
    }
    .category-list {
        display: flex;
        justify-content: center;
        list-style: none;
        margin: 0;
        padding: 0;
        flex-wrap: wrap;
    }
    .category-link {
        display: block;
        padding: 14px 20px;
        color: #4a4a4a;
        font-weight: 600;
        font-size: 13px;
        text-decoration: none;
        text-transform: uppercase;
        letter-spacing: 0.5px;
        transition: color 0.3s ease;
    }
    .category-link:hover { color: #198754; }
    .category-link i { margin-left: 5px; font-size: 11px; transition: transform 0.3s; }
    .category-link:hover i { transform: rotate(180deg); }
    .promo-link { color: #dc3545 !important; }
    .promo-link:hover { color: #bd2130 !important; }
    .transition-hover { transition: all 0.3s ease; }
    .transition-hover:hover { transform: translateY(-5px); box-shadow: 0 .5rem 1rem rgba(0,0,0,.15)!important; }
    .hover-success:hover { color: #198754 !important; }
</style>

<!-- CATEGORY NAV BAR -->
<nav class="category-nav-wrapper">
    <div class="container">
        <ul class="category-list">
            <c:if test="${not empty categories}">
                <c:forEach var="category" items="${categories}" varStatus="status">
                    <li class="category-item">
                        <a href="${pageContext.request.contextPath}/flowers?categoryID=${category.categoryID}"
                           class="category-link ${status.index == categories.size() - 1 ? 'promo-link' : ''}">
                            ${category.categoryName}
                            <c:if test="${status.index != categories.size() - 1}">
                                <i class="bi bi-chevron-down"></i>
                            </c:if>
                        </a>
                    </li>
                </c:forEach>
            </c:if>
            <c:if test="${empty categories}">
                <li class="category-item"><a href="${pageContext.request.contextPath}/flowers" class="category-link">Tất cả sản phẩm</a></li>
            </c:if>
        </ul>
    </div>
</nav>

<!-- HERO CAROUSEL -->
<div id="heroCarousel" class="carousel slide mb-5 shadow-sm" data-bs-ride="carousel">
    <div class="carousel-indicators">
        <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="0" class="active" aria-current="true"></button>
        <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="1"></button>
    </div>
    <div class="carousel-inner">
        <div class="carousel-item active" style="height: 400px; background-color: #e8f5e9;">
            <div class="container h-100 d-flex align-items-center justify-content-center text-center">
                <div>
                    <h1 class="display-4 fw-bold text-success mb-3">Trao Gửi Yêu Thương</h1>
                    <p class="lead text-dark mb-4">Mẫu hoa thiết kế tinh tế, giao hàng hỏa tốc trong 2H</p>
                    <a href="${pageContext.request.contextPath}/flowers" class="btn btn-success btn-lg rounded-pill px-5 shadow">Mua Hoa Ngay</a>
                </div>
            </div>
        </div>
        <div class="carousel-item" style="height: 400px; background-color: #fff3e0;">
            <div class="container h-100 d-flex align-items-center justify-content-center text-center">
                <div>
                    <h1 class="display-4 fw-bold text-warning mb-3">Hoa Sinh Nhật Đẹp</h1>
                    <p class="lead text-dark mb-4">Tặng kèm thiệp cao cấp & Banner thiết kế riêng</p>
                    <a href="${pageContext.request.contextPath}/flowers" class="btn btn-warning text-white btn-lg rounded-pill px-5 shadow">Xem Mẫu</a>
                </div>
            </div>
        </div>
    </div>
    <button class="carousel-control-prev" type="button" data-bs-target="#heroCarousel" data-bs-slide="prev">
        <span class="carousel-control-prev-icon" aria-hidden="true" style="filter: invert(100%);"></span>
        <span class="visually-hidden">Trước</span>
    </button>
    <button class="carousel-control-next" type="button" data-bs-target="#heroCarousel" data-bs-slide="next">
        <span class="carousel-control-next-icon" aria-hidden="true" style="filter: invert(100%);"></span>
        <span class="visually-hidden">Sau</span>
    </button>
</div>

<div class="container">
    <!-- QUICK FEATURES -->
    <div class="row text-center mb-5 g-4">
        <div class="col-md-3 col-6">
            <div class="p-3 border rounded-3 bg-light h-100 transition-hover">
                <i class="bi bi-clock-history text-success mb-2" style="font-size: 2.5rem;"></i>
                <h6 class="fw-bold">Giao Hỏa Tốc 2H</h6>
                <p class="small text-muted mb-0">Nội thành TP.HCM, HN</p>
            </div>
        </div>
        <div class="col-md-3 col-6">
            <div class="p-3 border rounded-3 bg-light h-100 transition-hover">
                <i class="bi bi-truck text-success mb-2" style="font-size: 2.5rem;"></i>
                <h6 class="fw-bold">Freeship</h6>
                <p class="small text-muted mb-0">Cho đơn hàng trên 500k</p>
            </div>
        </div>
        <div class="col-md-3 col-6">
            <div class="p-3 border rounded-3 bg-light h-100 transition-hover">
                <i class="bi bi-card-text text-success mb-2" style="font-size: 2.5rem;"></i>
                <h6 class="fw-bold">Tặng Thiệp Miễn Phí</h6>
                <p class="small text-muted mb-0">Kèm theo mỗi đơn hàng</p>
            </div>
        </div>
        <div class="col-md-3 col-6">
            <div class="p-3 border rounded-3 bg-light h-100 transition-hover">
                <i class="bi bi-flower1 text-success mb-2" style="font-size: 2.5rem;"></i>
                <h6 class="fw-bold">Hoa Tươi 3 Ngày</h6>
                <p class="small text-muted mb-0">Cam kết 100% tươi mới</p>
            </div>
        </div>
    </div>

    <!-- FEATURED PRODUCTS -->
    <div class="d-flex justify-content-between align-items-end mb-4 border-bottom pb-2">
        <h3 class="fw-bold text-success mb-0">
            <i class="bi bi-stars text-warning me-2"></i>Sản Phẩm Nổi Bật
        </h3>
        <a href="${pageContext.request.contextPath}/flowers" class="text-success text-decoration-none fw-medium">Xem tất cả &raquo;</a>
    </div>

    <div class="row row-cols-1 row-cols-sm-2 row-cols-md-4 g-4 mb-5">
        <c:if test="${empty featuredFlowers}">
            <div class="col-12 text-center text-muted w-100">
                <p>Chưa có sản phẩm nổi bật nào.</p>
            </div>
        </c:if>

        <c:forEach var="flower" items="${featuredFlowers}">
            <div class="col">
                <div class="card h-100 shadow-sm border-0 product-card transition-hover position-relative">
                    <span class="badge bg-danger position-absolute top-0 start-0 m-2 px-2 py-1 shadow-sm z-1" style="font-size: 0.8rem;">
                        HOT
                    </span>
                    <a href="${pageContext.request.contextPath}/flower?id=${flower.flowerID}" class="overflow-hidden rounded-top">
                        <img src="${pageContext.request.contextPath}/images/flowers/${flower.image}"
                             class="card-img-top p-2"
                             style="height: 260px; object-fit: cover; border-radius: 16px; transition: transform 0.3s;"
                             alt="${flower.flowerName}"
                             onmouseover="this.style.transform='scale(1.05)'"
                             onmouseout="this.style.transform='scale(1)'">
                    </a>
                    <div class="card-body text-center d-flex flex-column justify-content-between">
                        <div>
                            <h6 class="card-title fw-bold mb-2">
                                <a href="${pageContext.request.contextPath}/flower?id=${flower.flowerID}" class="text-decoration-none text-dark hover-success">
                                    ${flower.flowerName}
                                </a>
                            </h6>
                            <p class="card-text text-danger fw-bold fs-5 mb-3">
                                <fmt:formatNumber value="${flower.price}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                            </p>
                        </div>
                        <form action="${pageContext.request.contextPath}/cart" method="post" class="mt-auto">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="flowerID" value="${flower.flowerID}">
                            <input type="hidden" name="quantity" value="1">
                            <button type="submit" class="btn btn-outline-success w-100 fw-bold rounded-pill shadow-sm">
                                <i class="bi bi-cart-plus-fill me-1"></i> Đặt Hàng
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />