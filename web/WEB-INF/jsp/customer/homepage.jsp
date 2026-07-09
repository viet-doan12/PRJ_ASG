<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Nhúng Header dùng chung của nhóm -->
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<!-- CSS tùy chỉnh riêng cho phần Category (giống Flowercorner) -->
<style>
    /* Category Navigation Bar Styling (Replicating flowercorner.vn) */
    .category-nav-wrapper {
        background-color: #ffffff;
        position: sticky;
        top: 56px; /* Doan's navbar height is roughly 56px, this keeps it sticky right below the main header */
        z-index: 1020;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
        transition: all 0.3s ease;
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
    
    .category-item {
        position: relative;
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
    
    .category-link:hover {
        color: #198754; /* Match bootstrap success green / brand color of Doan's header */
    }
    
    /* Dropdown Icon effect */
    .category-link i {
        margin-left: 5px;
        font-size: 11px;
        transition: transform 0.3s;
    }
    
    .category-link:hover i {
        transform: rotate(180deg);
    }
    
    /* Promotion Highlight (Red color) */
    .promo-link {
        color: #dc3545 !important;
    }
    .promo-link:hover {
        color: #bd2130 !important;
    }

    /* Hero Banner mockup */
    .hero-banner {
        background: linear-gradient(rgba(0, 0, 0, 0.3), rgba(0, 0, 0, 0.3)), url('https://images.unsplash.com/photo-1563241527-3004b7be0ffd?ixlib=rb-4.0.3&auto=format&fit=crop&w=1920&q=80') center/cover;
        height: 380px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        text-align: center;
        margin-bottom: 40px;
    }
    
    .hero-title {
        font-size: 2.8rem;
        font-weight: 700;
        margin-bottom: 12px;
        text-shadow: 2px 2px 4px rgba(0,0,0,0.5);
    }
    
    .hero-subtitle {
        font-size: 1.1rem;
        text-shadow: 1px 1px 2px rgba(0,0,0,0.5);
    }
</style>

<!-- THIẾT KẾ PHẦN CATEGORY THEO YÊU CẦU CỦA BẠN (GIỐNG FLOWER CORNER) -->
<nav class="category-nav-wrapper">
    <div class="container">
        <ul class="category-list">
            <c:if test="${not empty categories}">
                <c:forEach var="category" items="${categories}" varStatus="status">
                    <li class="category-item">
                        <!-- Làm nổi bật danh mục cuối cùng bằng chữ đỏ giống "Giảm giá 30%" ở flowercorner -->
                        <a href="${pageContext.request.contextPath}/home?categoryId=${category.categoryID}" 
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
                <li class="category-item"><a href="#" class="category-link">HOA SINH NHẬT <i class="bi bi-chevron-down"></i></a></li>
                <li class="category-item"><a href="#" class="category-link">HOA KHAI TRƯƠNG <i class="bi bi-chevron-down"></i></a></li>
                <li class="category-item"><a href="#" class="category-link">LAN HỒ ĐIỆP <i class="bi bi-chevron-down"></i></a></li>
                <li class="category-item"><a href="#" class="category-link">CHỦ ĐỀ <i class="bi bi-chevron-down"></i></a></li>
                <li class="category-item"><a href="#" class="category-link promo-link">GIẢM 30%</a></li>
            </c:if>
        </ul>
    </div>
</nav>
<!-- KẾT THÚC PHẦN CATEGORY -->

<!-- Hero Banner -->
<section class="hero-banner">
    <div>
        <h1 class="hero-title">Trao Gửi Yêu Thương</h1>
        <p class="hero-subtitle">Dịch vụ điện hoa tận nơi uy tín, chất lượng nhất</p>
    </div>
</section>

<!-- Main Content Area -->
<main class="container my-5">
    <div class="row">
        <div class="col-12 text-center">
            <h2 class="mb-3 fw-bold">Sản Phẩm Nổi Bật</h2>
            <p class="text-muted">Chọn một danh mục ở thanh menu phía trên để lọc sản phẩm.</p>
        </div>
    </div>
</main>

<!-- Nhúng Footer dùng chung của nhóm -->
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
