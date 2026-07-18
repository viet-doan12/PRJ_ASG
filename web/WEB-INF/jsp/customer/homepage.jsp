<%-- 
    Document   : homepage
    Author     : Group 6
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<style>
    :root {
        --brand-green: #198754;
        --brand-green-dark: #146c43;
        --brand-pink: #d63384;
        --text-dark: #1f2937;
        --text-muted: #6b7280;
        --radius-md: 14px;
        --radius-lg: 22px;
        --shadow-sm: 0 2px 8px rgba(0,0,0,0.05);
        --shadow-hover: 0 14px 32px rgba(25,135,84,0.16);
    }

    /* ===== HERO ===== */
    .hero-banner {
        position: relative;
        height: 460px;
        border-radius: 0 0 var(--radius-lg) var(--radius-lg);
        overflow: hidden;
        background: linear-gradient(135deg, #e8f5e9 0%, #f1f8e9 100%);
        display: flex;
        align-items: center;
        margin-bottom: 3rem;
    }

    .hero-banner::before {
        content: '';
        position: absolute;
        inset: 0;
        background: radial-gradient(circle at 80% 30%, rgba(25,135,84,0.10) 0%, transparent 55%);
    }

    .hero-content {
        position: relative;
        z-index: 3;
    }

    /* ===== HOA TRANG TRÍ LƠ LỬNG TRONG NỀN ===== */
    .hero-decor-flower {
        position: absolute;
        z-index: 1;
        opacity: 0.35;
        user-select: none;
        pointer-events: none;
        animation: floatFlower 6s ease-in-out infinite;
    }

    .hero-decor-flower.f1 {
        top: 8%;
        right: 8%;
        font-size: 5rem;
        animation-delay: 0s;
    }
    .hero-decor-flower.f2 {
        top: 55%;
        right: 20%;
        font-size: 3.2rem;
        animation-delay: 1.2s;
        opacity: 0.25;
    }
    .hero-decor-flower.f3 {
        top: 20%;
        right: 32%;
        font-size: 2.4rem;
        animation-delay: 2.4s;
        opacity: 0.3;
    }
    .hero-decor-flower.f4 {
        top: 72%;
        right: 6%;
        font-size: 4rem;
        animation-delay: 0.6s;
        opacity: 0.2;
    }
    .hero-decor-flower.f5 {
        top: 38%;
        right: 4%;
        font-size: 2rem;
        animation-delay: 3s;
        opacity: 0.28;
    }

    @keyframes floatFlower {
        0%, 100% {
            transform: translateY(0) rotate(0deg);
        }
        50%      {
            transform: translateY(-18px) rotate(8deg);
        }
    }

    /* Chấm bi trang trí nhỏ rải rác thêm chiều sâu */
    .hero-banner .dot-decor {
        position: absolute;
        border-radius: 50%;
        background: var(--brand-green);
        opacity: 0.08;
        z-index: 1;
    }

    .dot-decor.d1 {
        width: 120px;
        height: 120px;
        top: -40px;
        right: 25%;
    }
    .dot-decor.d2 {
        width: 60px;
        height: 60px;
        bottom: 10%;
        right: 45%;
    }
    .dot-decor.d3 {
        width: 200px;
        height: 200px;
        top: 30%;
        right: -60px;
    }

    /* ===== QUICK FEATURES ===== */
    .feature-item {
        background: #fff;
        border-radius: var(--radius-md);
        padding: 1.75rem 1.25rem;
        text-align: center;
        box-shadow: var(--shadow-sm);
        transition: all 0.25s ease;
        height: 100%;
    }

    .feature-item:hover {
        transform: translateY(-4px);
        box-shadow: var(--shadow-hover);
    }

    .feature-icon {
        width: 56px;
        height: 56px;
        border-radius: 50%;
        background: #e8f5e9;
        color: var(--brand-green);
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.5rem;
        margin: 0 auto 1rem;
    }

    .feature-item h6 {
        font-weight: 700;
        color: var(--text-dark);
        margin-bottom: 0.3rem;
    }

    /* ===== SECTION TITLE ===== */
    .section-title {
        font-size: 1.65rem;
        font-weight: 700;
        color: var(--text-dark);
        position: relative;
        padding-left: 16px;
        margin-bottom: 0;
    }

    .section-title::before {
        content: '';
        position: absolute;
        left: 0;
        top: 3px;
        bottom: 3px;
        width: 4px;
        background: var(--brand-green);
        border-radius: 4px;
    }

    .section-link {
        color: var(--brand-green);
        font-weight: 600;
        text-decoration: none;
        font-size: 0.95rem;
    }

    .section-link:hover {
        color: var(--brand-green-dark);
    }

    /* ===== PRODUCT CARD ===== */
    .product-card {
        border: none;
        border-radius: var(--radius-md);
        overflow: hidden;
        background: #fff;
        transition: transform 0.25s ease, box-shadow 0.25s ease;
        position: relative;
    }

    .product-card:hover {
        transform: translateY(-6px);
        box-shadow: var(--shadow-hover);
    }

    .product-card .img-wrap {
        aspect-ratio: 1 / 1;
        overflow: hidden;
        background: #f3f4f6;
    }

    .product-card .img-wrap img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.45s ease;
    }

    .product-card:hover .img-wrap img {
        transform: scale(1.08);
    }

    .product-card .hot-badge {
        position: absolute;
        top: 12px;
        left: 12px;
        background: #dc3545;
        color: #fff;
        font-size: 0.7rem;
        font-weight: 700;
        padding: 3px 10px;
        border-radius: 999px;
        z-index: 2;
        letter-spacing: 0.03em;
    }

    .product-card .card-body {
        padding: 1.1rem;
        text-align: center;
    }

    .product-card .product-name {
        font-weight: 600;
        color: var(--text-dark);
        text-decoration: none;
        display: block;
        margin-bottom: 0.4rem;
        font-size: 0.95rem;
        min-height: 2.4em;
    }

    .product-card .product-name:hover {
        color: var(--brand-green);
    }

    .product-card .price {
        color: var(--brand-pink);
        font-weight: 700;
        font-size: 1.05rem;
        margin-bottom: 0.85rem;
    }

    .btn-add-cart {
        border-radius: 999px;
        font-weight: 600;
        padding: 0.5rem 1rem;
        border: 1.5px solid var(--brand-green);
        color: var(--brand-green);
        background: #fff;
        transition: all 0.2s ease;
        width: 100%;
    }

    .btn-add-cart:hover {
        background: var(--brand-green);
        color: #fff;
    }

    .empty-state {
        text-align: center;
        padding: 3rem 1rem;
        color: var(--text-muted);
    }
    .stock-badge {
        position: absolute;
        top: 12px;
        right: 12px;
        z-index: 2;
        font-size: 0.72rem;
        font-weight: 700;
        padding: 4px 11px;
        border-radius: 999px;
        letter-spacing: 0.02em;
    }

    .stock-badge.in-stock {
        background: #e8f5e9;
        color: var(--brand-green-dark);
    }

    .stock-badge.low-stock {
        background: #fff3cd;
        color: #997404;
    }

    .stock-badge.out-of-stock {
        background: #f8d7da;
        color: #b02a37;
    }
</style>

<!-- HERO -->
<div class="container">
    <div class="hero-banner px-5">
        <!-- Nền trang trí -->
        <div class="dot-decor d1"></div>
        <div class="dot-decor d2"></div>
        <div class="dot-decor d3"></div>
        <span class="hero-decor-flower f1">🌸</span>
        <span class="hero-decor-flower f2">🌺</span>
        <span class="hero-decor-flower f3">🌷</span>
        <span class="hero-decor-flower f4">🌼</span>
        <span class="hero-decor-flower f5">🌹</span>

        <div class="hero-content">
            <h1 class="hero-title">Trao Gửi<br>Yêu Thương</h1>
            <p class="hero-subtitle">Mẫu hoa thiết kế tinh tế, giao hàng hỏa tốc trong 2 giờ. Mỗi bó hoa là một câu chuyện được chăm chút riêng cho bạn.</p>
            <a href="${pageContext.request.contextPath}/flowers" class="btn btn-success hero-cta">
                Mua Hoa Ngay <i class="bi bi-arrow-right ms-1"></i>
            </a>
        </div>
    </div>
</div>

<div class="container">

    <!-- QUICK FEATURES -->
    <div class="row g-3 mb-5">
        <div class="col-md-3 col-6">
            <div class="feature-item">
                <div class="feature-icon"><i class="bi bi-clock-history"></i></div>
                <h6>Giao Hỏa Tốc 2H</h6>
                <p class="small text-muted mb-0">Nội thành TP.HCM, HN</p>
            </div>
        </div>
        <div class="col-md-3 col-6">
            <div class="feature-item">
                <div class="feature-icon"><i class="bi bi-truck"></i></div>
                <h6>Freeship</h6>
                <p class="small text-muted mb-0">Đơn hàng trên 500k</p>
            </div>
        </div>
        <div class="col-md-3 col-6">
            <div class="feature-item">
                <div class="feature-icon"><i class="bi bi-card-text"></i></div>
                <h6>Thiệp Miễn Phí</h6>
                <p class="small text-muted mb-0">Kèm theo mỗi đơn hàng</p>
            </div>
        </div>
        <div class="col-md-3 col-6">
            <div class="feature-item">
                <div class="feature-icon"><i class="bi bi-flower1"></i></div>
                <h6>Hoa Tươi 3 Ngày</h6>
                <p class="small text-muted mb-0">Cam kết 100% tươi mới</p>
            </div>
        </div>
    </div>

    <!-- FEATURED PRODUCTS -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="section-title">Sản Phẩm Nổi Bật</h2>
        <a href="${pageContext.request.contextPath}/flowers" class="section-link">Xem tất cả <i class="bi bi-arrow-right"></i></a>
    </div>

    <div class="row row-cols-2 row-cols-sm-2 row-cols-md-4 g-4 mb-5">

        <c:if test="${empty featuredFlowers}">
            <div class="col-12">
                <div class="empty-state">
                    <i class="bi bi-flower1" style="font-size:3rem;color:#d1d5db;"></i>
                    <p class="mt-3 mb-0">Chưa có sản phẩm nổi bật nào.</p>
                </div>
            </div>
        </c:if>

        <c:forEach var="flower" items="${featuredFlowers}">
            <div class="col">
                <div class="card product-card h-100">
                    <span class="hot-badge">HOT</span>
                    <a href="${pageContext.request.contextPath}/flower?id=${flower.flowerID}" class="img-wrap d-block position-relative">
                        <c:choose>
                            <c:when test="${flower.stockQuantity <= 0}">
                                <span class="stock-badge out-of-stock">Hết hàng</span>
                            </c:when>
                            <c:when test="${flower.stockQuantity <= 5}">
                                <span class="stock-badge low-stock">Còn ${flower.stockQuantity} sản phẩm</span>
                            </c:when>
                            <c:otherwise>
                                <span class="stock-badge in-stock">Còn ${flower.stockQuantity} sản phẩm</span>
                            </c:otherwise>
                        </c:choose>
                        <img src="${pageContext.request.contextPath}/images/flowers/${flower.image}" alt="${flower.flowerName}">
                    </a>
                    <div class="card-body">
                        <a href="${pageContext.request.contextPath}/flower?id=${flower.flowerID}" class="product-name">
                            ${flower.flowerName}
                        </a>
                        <div class="price">
                            <fmt:formatNumber value="${flower.price}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                        </div>
                        <form action="${pageContext.request.contextPath}/cart" method="post">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="flowerID" value="${flower.flowerID}">
                            <input type="hidden" name="quantity" value="1">
                            <button type="submit" class="btn-add-cart">
                                <i class="bi bi-cart-plus me-1"></i> Thêm vào giỏ
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </c:forEach>

    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />