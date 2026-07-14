<%-- 
    Document   : cart
    Created on : Jul 4, 2026, 7:35:11 PM
    Author     : ADMIN
--%>

<%--
    Document   : cart.jsp
    Mô tả      : Trang giỏ hàng của Customer.
                 Nhận dữ liệu từ CartServlet qua session:
                   - sessionScope.cart      -> List<CartItem>
                   - sessionScope.cartTotal -> BigDecimal
                 Các action gọi lại CartServlet (/cart): update, delete, clear
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<div class="cart-page mb-5">
    <h2 class="fw-bold mb-4"><i class="bi bi-cart3 me-2"></i>Giỏ hàng của bạn</h2>

    <c:choose>
        <%-- ========== GIỎ HÀNG TRỐNG ========== --%>
        <c:when test="${empty sessionScope.cart}">
            <div class="text-center py-5">
                <i class="bi bi-cart-x display-1 text-muted"></i>
                <p class="fs-5 text-muted mt-3">Giỏ hàng của bạn đang trống.</p>
                <a href="${pageContext.request.contextPath}/flowers" class="btn btn-success mt-2">
                    <i class="bi bi-shop me-1"></i> Tiếp tục mua sắm
                </a>
            </div>
        </c:when>

        <%-- ========== CÓ SẢN PHẨM TRONG GIỎ ========== --%>
        <c:otherwise>
            <div class="row g-4">
                <!-- Danh sách sản phẩm -->
                <div class="col-lg-8">
                    <div class="card border-0 shadow-sm">
                        <div class="table-responsive">
                            <table class="table align-middle mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th class="ps-3">Sản phẩm</th>
                                        <th class="text-end">Đơn giá</th>
                                        <th class="text-center" style="width:150px;">Số lượng</th>
                                        <th class="text-end">Thành tiền</th>
                                        <th class="text-center pe-3">Xóa</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="item" items="${sessionScope.cart}">
                                        <c:set var="flower" value="${item.flower}" />
                                        <tr>
                                            <td class="ps-3">
                                                <div class="d-flex align-items-center">
                                                    <img src="${pageContext.request.contextPath}/images/flowers/${flower.image}"
                                                         onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/images/flowers/no-image.png';"
                                                         alt="${flower.flowerName}"
                                                         class="rounded me-3 border"
                                                         style="width:64px;height:64px;object-fit:cover;">
                                                    <div>
                                                        <div class="fw-semibold">${flower.flowerName}</div>
                                                        <c:if test="${item.quantity > flower.stockQuantity}">
                                                            <div class="small text-danger mt-1">
                                                                <i class="bi bi-exclamation-triangle-fill"></i>
                                                                Chỉ còn ${flower.stockQuantity} sản phẩm trong kho
                                                            </div>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </td>
                                            <td class="text-end">
                                                <fmt:formatNumber value="${flower.price}" type="number" groupingUsed="true"/>&nbsp;đ
                                            </td>
                                            <td>
                                                <form action="${pageContext.request.contextPath}/cart" method="post"
                                                      class="d-flex justify-content-center align-items-center gap-1">
                                                    <input type="hidden" name="action" value="update">
                                                    <input type="hidden" name="flowerID" value="${flower.flowerID}">
                                                    <button type="button" class="btn btn-sm btn-outline-secondary px-2"
                                                            title="Giảm số lượng"
                                                            onclick="var i = this.nextElementSibling; var v = parseInt(i.value) - 1; if (v >= 1) {
                                                                        i.value = v;
                                                                        this.form.submit();
                                                                    }">
                                                        <i class="bi bi-dash"></i>
                                                    </button>
                                                    <input type="number" name="quantity" value="${item.quantity}"
                                                           min="1" max="${flower.stockQuantity}"
                                                           class="form-control form-control-sm text-center px-1"
                                                           style="width:52px;"
                                                           onchange="var v = parseInt(this.value); var max =${flower.stockQuantity}; if (isNaN(v) || v < 1)
                                                                       v = 1;
                                                                   if (v > max)
                                                                       v = max;
                                                                   this.value = v;
                                                                   this.form.submit();">
                                                    <button type="button" class="btn btn-sm btn-outline-secondary px-2"
                                                            title="Tăng số lượng"
                                                            ${item.quantity >= flower.stockQuantity ? 'disabled' : ''}
                                                            onclick="var i = this.previousElementSibling;
                                                                    var v = parseInt(i.value) + 1;
                                                                    var max =${flower.stockQuantity};
                                                                    if (v <= max) {
                                                                        i.value = v;
                                                                        this.form.submit();
                                                                    }">
                                                        <i class="bi bi-plus"></i>
                                                    </button>
                                                </form>
                                            </td>
                                            <td class="text-end fw-semibold text-success">
                                                <fmt:formatNumber value="${flower.price * item.quantity}" type="number" groupingUsed="true"/>&nbsp;đ
                                            </td>
                                            <td class="text-center pe-3">
                                                <form action="${pageContext.request.contextPath}/cart" method="post"
                                                      onsubmit="return confirm('Xóa sản phẩm \'${flower.flowerName}\' khỏi giỏ hàng?');">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="flowerID" value="${flower.flowerID}">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger" title="Xóa sản phẩm">
                                                        <i class="bi bi-trash"></i>
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <div class="d-flex justify-content-between mt-3">
                        <a href="${pageContext.request.contextPath}/flowers" class="btn btn-outline-success">
                            <i class="bi bi-arrow-left me-1"></i> Tiếp tục mua sắm
                        </a>
                        <form action="${pageContext.request.contextPath}/cart" method="post"
                              onsubmit="return confirm('Xóa toàn bộ giỏ hàng?');">
                            <input type="hidden" name="action" value="clear">
                            <button type="submit" class="btn btn-outline-danger">
                                <i class="bi bi-x-circle me-1"></i> Xóa toàn bộ giỏ hàng
                            </button>
                        </form>
                    </div>
                </div>

                <!-- Tổng đơn hàng -->
                <div class="col-lg-4">
                    <div class="card border-0 shadow-sm">
                        <div class="card-body">
                            <h5 class="fw-bold mb-3">Tổng đơn hàng</h5>

                            <div class="d-flex justify-content-between mb-2">
                                <span class="text-muted">Tạm tính</span>
                                <span><fmt:formatNumber value="${sessionScope.cartTotal}" type="number" groupingUsed="true"/>&nbsp;đ</span>
                            </div>
                            <div class="d-flex justify-content-between mb-2 text-muted small">
                                <span>Phí vận chuyển</span>
                                <span>Tính khi thanh toán</span>
                            </div>

                            <hr>

                            <div class="d-flex justify-content-between fw-bold fs-5 mb-3">
                                <span>Tổng cộng</span>
                                <span class="text-success">
                                    <fmt:formatNumber value="${sessionScope.cartTotal}" type="number" groupingUsed="true"/>&nbsp;đ
                                </span>
                            </div>

                            <a href="${pageContext.request.contextPath}/checkout" class="btn btn-success w-100">
                                <i class="bi bi-credit-card me-1"></i> Tiến hành thanh toán
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

