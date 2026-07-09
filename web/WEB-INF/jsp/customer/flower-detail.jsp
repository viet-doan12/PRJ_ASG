<%-- 
    Document   : flower-detail
    Created on : Jul 4, 2026, 7:35:04 PM
    Author     : ADMIN
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<jsp:include page="header.jsp" />

<div class="container my-5">
    

    <c:if test="${empty flower}">
        <div class="text-center py-5">
            <i class="bi bi-exclamation-triangle text-warning" style="font-size: 5rem;"></i>
            <h3 class="mt-3">Không tìm thấy sản phẩm!</h3>
            <p class="text-muted">Sản phẩm này có thể đã bị xóa hoặc không tồn tại.</p>
            <a href="${pageContext.request.contextPath}/flowers" class="btn btn-success rounded-pill mt-3 px-4">
                Quay lại cửa hàng
            </a>
        </div>
    </c:if>


    <c:if test="${not empty flower}">
        

        <nav aria-label="breadcrumb" class="mb-4">
            <ol class="breadcrumb">
                <li class="breadcrumb-item">
                    <a href="${pageContext.request.contextPath}/" class="text-success text-decoration-none">
                        <i class="bi bi-house-door-fill"></i> Trang chủ
                    </a>
                </li>
                <li class="breadcrumb-item">
                    <a href="${pageContext.request.contextPath}/flowers" class="text-success text-decoration-none">
                        Cửa hàng
                    </a>
                </li>
                <li class="breadcrumb-item active" aria-current="page">${flower.flowerName}</li>
            </ol>
        </nav>


        <div class="row bg-white p-4 rounded-4 shadow-sm mb-5">
            
     
            <div class="col-md-5 mb-4 mb-md-0 text-center">
                <img src="${pageContext.request.contextPath}/images/flowers/${flower.image}" 
                     class="img-fluid rounded-3 shadow-sm w-100" 
                     style="max-height: 500px; object-fit: cover;" 
                     alt="${flower.flowerName}">
            </div>

   
            <div class="col-md-7 ps-md-5 d-flex flex-column justify-content-between">
                <div>
  
                    <h2 class="fw-bold text-dark mb-2">${flower.flowerName}</h2>
                    <div class="mb-3">
                        <span class="badge ${flower.stockQuantity > 0 && flower.status ? 'bg-success' : 'bg-danger'} rounded-pill px-3 py-2">
                            <i class="bi ${flower.stockQuantity > 0 && flower.status ? 'bi-check-circle' : 'bi-x-circle'} me-1"></i>
                            ${flower.stockQuantity > 0 && flower.status ? 'Còn hàng' : 'Hết hàng hoặc Ngừng kinh doanh'}
                        </span>
                        <span class="text-muted ms-2 small">| Mã SP: FLW-${flower.flowerID}</span>
                    </div>

 
                    <h3 class="text-danger fw-bold mb-4">
                        <fmt:formatNumber value="${flower.price}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                    </h3>


                    <p class="text-secondary lh-lg mb-4">
                        ${not empty flower.description ? flower.description : 'Chưa có mô tả chi tiết cho sản phẩm này.'}
                    </p>
                    
                    <hr class="text-muted">

 
                    <div class="bg-light p-3 rounded-3 mb-4">
                        <ul class="list-unstyled mb-0" style="font-size: 0.95rem;">
                            <li class="mb-2"><i class="bi bi-gift text-danger me-2"></i>Tặng kèm thiệp chúc mừng, băng rôn miễn phí</li>
                            <li class="mb-2"><i class="bi bi-truck text-success me-2"></i>Giao hàng hỏa tốc trong vòng 2 giờ</li>
                            <li><i class="bi bi-flower1 text-primary me-2"></i>Cam kết hoa tươi mới ít nhất 3 ngày</li>
                        </ul>
                    </div>
                </div>

                <form action="${pageContext.request.contextPath}/cart" method="post" class="mt-auto">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="flowerID" value="${flower.flowerID}">
                    
                    <div class="d-flex align-items-center gap-3">
  
                        <div class="input-group" style="width: 130px;">
                            <span class="input-group-text bg-white">SL:</span>
                            <input type="number" name="quantity" class="form-control text-center fw-bold" 
                                   value="1" min="1" max="${flower.stockQuantity}" 
                                   ${flower.stockQuantity == 0 || !flower.status ? 'disabled' : ''}>
                        </div>
                        
                        <button type="submit" class="btn btn-success btn-lg flex-grow-1 fw-bold rounded-pill shadow-sm"
                                ${flower.stockQuantity == 0 || !flower.status ? 'disabled' : ''}>
                            <i class="bi bi-cart-plus-fill me-2"></i> 
                            ${flower.stockQuantity > 0 && flower.status ? 'Thêm Vào Giỏ Hàng' : 'Tạm Hết Hàng'}
                        </button>
                    </div>
                </form>

            </div>
        </div>

        <c:if test="${not empty relatedFlowers}">
            <div class="mt-5 border-top pt-4">
                <h4 class="fw-bold text-success mb-4">Sản Phẩm Tương Tự</h4>
                <div class="row row-cols-1 row-cols-sm-2 row-cols-md-4 g-4">
                    <c:forEach var="relFlower" items="${relatedFlowers}">
                        <div class="col">
                            <div class="card h-100 shadow-sm border-0 product-card transition-hover">
                                <a href="${pageContext.request.contextPath}/flower?id=${relFlower.flowerID}" class="overflow-hidden rounded-top">
                                    <img src="${pageContext.request.contextPath}/images/flowers/${relFlower.image}" 
                                         class="card-img-top p-2" 
                                         style="height: 200px; object-fit: cover; border-radius: 16px; transition: transform 0.3s;"
                                         alt="${relFlower.flowerName}"
                                         onmouseover="this.style.transform='scale(1.05)'" 
                                         onmouseout="this.style.transform='scale(1)'">
                                </a>
                                <div class="card-body text-center">
                                    <h6 class="fw-bold mb-2">
                                        <a href="${pageContext.request.contextPath}/flower?id=${relFlower.flowerID}" class="text-decoration-none text-dark hover-success">
                                            ${relFlower.flowerName}
                                        </a>
                                    </h6>
                                    <p class="text-danger fw-bold mb-0">
                                        <fmt:formatNumber value="${relFlower.price}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                                    </p>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:if>

    </c:if>
</div>

<style>
    .transition-hover { transition: all 0.3s ease; }
    .transition-hover:hover { transform: translateY(-5px); box-shadow: 0 .5rem 1rem rgba(0,0,0,.15)!important; }
    .hover-success:hover { color: #198754 !important; }
</style>


<jsp:include page="footer.jsp" />
