<%-- 
    Document   : flower
    Created on : Jul 4, 2026, 7:32:58 PM
    Author     : ADMIN
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="header.jsp" />

<div class="container my-5">
    <div class="row">
        
        <div class="col-lg-3 col-md-4 mb-4">
            <div class="card shadow-sm border-0">
                <div class="card-body">
                    <h5 class="fw-bold mb-3 text-success">
                        <i class="bi bi-list-ul me-2"></i>Danh mục hoa
                    </h5>
                    <div class="list-group list-group-flush">
                        <a href="${pageContext.request.contextPath}/flowers" 
                           class="list-group-item list-group-item-action fw-medium ${empty param.categoryID ? 'active text-white bg-success rounded' : ''}">
                            Tất cả sản phẩm
                        </a>
                        
                        <c:forEach var="cat" items="${categoryList}">
                            <a href="${pageContext.request.contextPath}/flowers?categoryID=${cat.categoryID}" 
                               class="list-group-item list-group-item-action fw-medium ${param.categoryID == cat.categoryID ? 'active text-white bg-success rounded' : ''}">
                                ${cat.categoryName}
                            </a>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-lg-9 col-md-8">
            
            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
                <h4 class="fw-bold text-dark mb-0">Danh Sách Sản Phẩm</h4>
                
                <div class="d-flex align-items-center gap-2">
                    <span class="text-secondary text-nowrap fw-medium">Sắp xếp:</span>
                    
                    <c:set var="catQuery" value="${not empty param.categoryID ? '&categoryID=' += param.categoryID : ''}" />
                    
                    <select class="form-select shadow-sm" style="width: auto; cursor: pointer;" name="sort" onchange="location = this.value;">
                        <option value="${pageContext.request.contextPath}/flowers?sort=default${catQuery}">Mặc định</option>
                        <option value="${pageContext.request.contextPath}/flowers?sort=price_asc${catQuery}" ${param.sort == 'price_asc' ? 'selected' : ''}>Giá: Thấp đến Cao</option>
                        <option value="${pageContext.request.contextPath}/flowers?sort=price_desc${catQuery}" ${param.sort == 'price_desc' ? 'selected' : ''}>Giá: Cao đến Thấp</option>
                        <option value="${pageContext.request.contextPath}/flowers?sort=name_asc${catQuery}" ${param.sort == 'name_asc' ? 'selected' : ''}>Tên: A - Z</option>
                    </select>
                </div>
            </div>

            <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 g-4">
                
                <c:if test="${empty flowerList}">
                    <div class="col-12 text-center my-5 py-5 w-100">
                        <i class="bi bi-flower1 text-muted" style="font-size: 4rem;"></i>
                        <h5 class="mt-3 text-secondary">Không tìm thấy sản phẩm phù hợp</h5>
                        <p class="text-muted">Vui lòng thử chọn danh mục hoặc bộ lọc khác.</p>
                    </div>
                </c:if>

                <c:forEach var="flower" items="${flowerList}">
                    <div class="col">
                        <div class="card h-100 shadow-sm border-0 product-card transition-hover">
                            
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
                                        <a href="${pageContext.request.contextPath}/flower?id=${flower.flowerID}" 
                                           class="text-decoration-none text-dark hover-success">
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

                <jsp:include page="pagination.jsp" />

            </div>

        </div>
    </div>
</div>

<style>
    .transition-hover { transition: all 0.3s ease; }
    .transition-hover:hover { transform: translateY(-5px); box-shadow: 0 .5rem 1rem rgba(0,0,0,.15)!important; }
    .hover-success:hover { color: #198754 !important; }
</style>

<jsp:include page="footer.jsp" />
