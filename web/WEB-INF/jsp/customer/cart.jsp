<%-- 
    Document   : cart
    Created on : Jul 4, 2026, 7:35:11 PM
    Author     : ADMIN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Giỏ hàng - FlowerShop</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<jsp:include page="../common/header.jsp"/>


<div class="cart-container">
    <h2>Giỏ hàng</h2>

    <c:if test="${not empty cartError}">
        <div class="alert alert-danger">${cartError}</div>
    </c:if>

    <c:choose>
        <%-- ============ TRƯỜNG HỢP GIỎ HÀNG TRỐNG ============ --%>
        <c:when test="${empty sessionScope.CART}">
            <p>Giỏ hàng của bạn đang trống!</p>
            <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">Tiếp tục mua hàng</a>
        </c:when>

        <%-- ============ TRƯỜNG HỢP CÓ SẢN PHẨM ============ --%>
        <c:otherwise>
            <table class="cart-table">
                <thead>
                <tr>
                    <th>Ảnh</th>
                    <th>Tên hoa</th>
                    <th>Đơn giá</th>
                    <th>Số lượng</th>
                    <th>Thành tiền</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="entry" items="${sessionScope.CART}">
                    <c:set var="item" value="${entry.value}"/>
                    <tr>
                        <td>
                            <img src="${pageContext.request.contextPath}/${item.image}"
                                 alt="${item.flowerName}" width="80"/>
                        </td>
                        <td>${item.flowerName}</td>
                        <td>
                            <fmt:formatNumber value="${item.unitPrice}" type="number" groupingUsed="true"/> VND
                        </td>
                        <td>
                            <!-- Cập nhật số lượng: submit form gọi lại CartServlet action=update -->
                            <form action="${pageContext.request.contextPath}/cart" method="post" class="qty-form">
                                <input type="hidden" name="action" value="update"/>
                                <input type="hidden" name="flowerId" value="${item.flowerId}"/>
                                <input type="number" name="quantity" value="${item.quantity}"
                                       min="1" max="${item.stockQuantity}"/>
                                <button type="submit" class="btn btn-sm">Cập nhật</button>
                            </form>
                        </td>
                        <td>
                            <fmt:formatNumber value="${item.subtotal}" type="number" groupingUsed="true"/> VND
                        </td>
                        <td>
                            <!-- Xóa sản phẩm khỏi giỏ -->
                            <form action="${pageContext.request.contextPath}/cart" method="post">
                                <input type="hidden" name="action" value="remove"/>
                                <input type="hidden" name="flowerId" value="${item.flowerId}"/>
                                <button type="submit" class="btn btn-sm btn-danger">Xóa</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <div class="cart-summary">
                <h3>
                    Tổng tiền:
                    <fmt:formatNumber value="${cartTotal}" type="number" groupingUsed="true"/> VND
                </h3>

                <form action="${pageContext.request.contextPath}/cart" method="post" style="display:inline;">
                    <input type="hidden" name="action" value="clear"/>
                    <button type="submit" class="btn btn-secondary">Xóa toàn bộ giỏ hàng</button>
                </form>

                <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary">
                    Tiến hành thanh toán
                </a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="footer.jsp"/>

</body>
</html>

