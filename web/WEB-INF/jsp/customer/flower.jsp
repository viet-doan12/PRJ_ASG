<%-- 
    Document   : flower
    Author     : ADMIN
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
        --radius-sm: 10px;
        --shadow-sm: 0 2px 8px rgba(0,0,0,0.05);
        --shadow-hover: 0 14px 32px rgba(25,135,84,0.16);
    }

    .filter-card {
        background: #fff;
        border-radius: var(--radius-md);
        box-shadow: var(--shadow-sm);
        padding: 1.5rem;
    }

    .filter-card h5 {
        font-weight: 700;
        color: var(--text-dark);
        margin-bottom: 1rem;
    }

    .category-filter-link {
        display: block;
        padding: 0.6rem 0.9rem;
        border-radius: var(--radius-sm);
        color: var(--text-dark);
        text-decoration: none;
        font-weight: 500;
        font-size: 0.92rem;
        transition: all 0.2s ease;
        margin-bottom: 0.25rem;
    }

    .category-filter-link:hover {
        background: #f0f9f4;
        color: var(--brand-green);
    }

    .category-filter-link.active {
        background: var(--brand-green);
        color: #fff;
    }

    .section-title {
        font-size: 1.5rem;
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

    .sort-select {
        border-radius: 999px;
        border: 1px solid #e5e7eb;
        padding: 0.5rem 1.2rem;
        font-size: 0.9rem;
        cursor: pointer;
    }

    /* ===== PRODUCT CARD (đồng bộ với homepage) ===== */
    .product-card {
        border: none;
        border-radius: var(--radius-md);
        overflow: hidden;
        background: #fff;
        transition: transform 0.25s ease, box-shadow 0.25s ease;
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
        padding: 4rem 1rem;
        color: var(--text-muted);
    }

    .empty-state i {
        font-size: 3.5rem;
        color: #d1d5db;
        margin-bottom: 1rem;
        display: block;
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

<div class="container my-5">
    <div class="row g-4">

        <div class="col-lg-3 col-md-4">
            <div class="filter-card">
                <h5><i class="bi bi-list-ul me-2 text-success"></i>Danh mục hoa</h5>

                <a href="${pageContext.request.contextPath}/flowers"
                   class="category-filter-link ${empty param.categoryID ? 'active' : ''}">
                    Tất cả sản phẩm
                </a>

                <c:forEach var="cat" items="${categoryList}">
                    <a href="${pageContext.request.contextPath}/flowers?categoryID=${cat.categoryID}"
                       class="category-filter-link ${param.categoryID == cat.categoryID ? 'active' : ''}">
                        ${cat.categoryName}
                    </a>
                </c:forEach>
            </div>
        </div>

        <div class="col-lg-9 col-md-8">

            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
                <h2 class="section-title">Danh Sách Sản Phẩm</h2>

                <c:choose>
                    <c:when test="${not empty param.categoryID}">
                        <c:set var="catQuery" value="&categoryID=${param.categoryID}" />
                    </c:when>
                    <c:otherwise>
                        <c:set var="catQuery" value="" />
                    </c:otherwise>
                </c:choose>

                <select class="form-select sort-select" style="width:auto;" name="sort" onchange="location = this.value;">
                    <option value="${pageContext.request.contextPath}/flowers?sort=default${catQuery}">Sắp xếp: Mặc định</option>
                    <option value="${pageContext.request.contextPath}/flowers?sort=price_asc${catQuery}" ${param.sort == 'price_asc' ? 'selected' : ''}>Giá: Thấp đến Cao</option>
                    <option value="${pageContext.request.contextPath}/flowers?sort=price_desc${catQuery}" ${param.sort == 'price_desc' ? 'selected' : ''}>Giá: Cao đến Thấp</option>
                    <option value="${pageContext.request.contextPath}/flowers?sort=name_asc${catQuery}" ${param.sort == 'name_asc' ? 'selected' : ''}>Tên: A - Z</option>
                </select>
            </div>

            <div class="row row-cols-2 row-cols-sm-2 row-cols-md-3 g-4">

                <c:if test="${empty flowerList}">
                    <div class="col-12">
                        <div class="empty-state">
                            <i class="bi bi-flower1"></i>
                            <h5 class="text-secondary">Không tìm thấy sản phẩm phù hợp</h5>
                            <p class="text-muted mb-0">Vui lòng thử chọn danh mục hoặc bộ lọc khác.</p>
                        </div>
                    </div>
                </c:if>

                <c:forEach var="flower" items="${flowerList}">
                    <div class="col">
                        <div class="card product-card h-100">
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
                                    <button type="submit" class="btn-add-cart" ${flower.stockQuantity <= 0 ? 'disabled' : ''}>
                                        <c:choose>
                                            <c:when test="${flower.stockQuantity <= 0}">
                                                <i class="bi bi-x-circle me-1"></i> Hết hàng
                                            </c:when>
                                            <c:otherwise>
                                                <i class="bi bi-cart-plus me-1"></i> Thêm vào giỏ
                                            </c:otherwise>
                                        </c:choose>
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </c:forEach>

            </div>

            <div class="mt-5 d-flex justify-content-center w-100">

                <c:set var="myBaseUrl" value="${pageContext.request.contextPath}/flowers" />
                <c:set var="isFirstParam" value="true" />

                <c:if test="${not empty param.categoryID}">
                    <c:set var="myBaseUrl" value="${myBaseUrl}?categoryID=${param.categoryID}" />
                    <c:set var="isFirstParam" value="false" />
                </c:if>

                <c:if test="${not empty param.sort}">
                    <c:choose>
                        <c:when test="${isFirstParam}">
                            <c:set var="myBaseUrl" value="${myBaseUrl}?sort=${param.sort}" />
                        </c:when>
                        <c:otherwise>
                            <c:set var="myBaseUrl" value="${myBaseUrl}&sort=${param.sort}" />
                        </c:otherwise>
                    </c:choose>
                </c:if>

                <c:set var="baseUrl" value="${myBaseUrl}" scope="request" />

                <jsp:include page="/WEB-INF/jsp/common/pagination.jsp" />

            </div>

        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />