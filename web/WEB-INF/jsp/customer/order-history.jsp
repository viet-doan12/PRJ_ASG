<%-- 
    Document   : order-history
    Created on : Jul 4, 2026, 7:36:03 PM
    Author     : ADMIN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>

<style>
    .order-title {
        font-weight: 700;
        color: #d63384;
        margin-bottom: 20px;
    }
    .order-card {
        background: #fff;
        border: 1px solid #f0e0e4;
        border-radius: 10px;
        padding: 16px 20px;
        margin-bottom: 14px;
        transition: box-shadow .2s ease;
    }
    .order-card:hover {
        box-shadow: 0 6px 16px rgba(214, 51, 132, .12);
    }
    .order-code {
        font-weight: 700;
        color: #333;
    }
    .order-price {
        font-weight: 700;
        color: #d63384;
        font-size: 17px;
    }
    .status-badge {
        font-size: 12px;
        padding: 4px 12px;
        border-radius: 14px;
        font-weight: 600;
    }
    .status-Pending   { background:#fff3cd; color:#856404; }
    .status-Confirmed { background:#cfe2ff; color:#084298; }
    .status-Shipping  { background:#d0ebff; color:#0b5ed7; }
    .status-Completed { background:#d4edda; color:#155724; }
    .status-Cancelled { background:#f8d7da; color:#842029; }
</style>

<h3 class="order-title"><i class="bi bi-receipt me-2"></i>Đơn hàng của tôi</h3>

<c:choose>
    <c:when test="${empty orderList}">
        <div class="text-center py-5">
            <i class="bi bi-inbox" style="font-size:48px;color:#ddd;"></i>
            <p class="text-muted mt-3 mb-4">Bạn chưa có đơn hàng nào.</p>
            <a href="${pageContext.request.contextPath}/flowers" class="btn" style="background:#d63384;color:#fff;border-radius:24px;padding:8px 28px;">
                Mua hoa ngay
            </a>
        </div>
    </c:when>
    <c:otherwise>
        <c:forEach var="o" items="${orderList}">
            <div class="order-card">
                <div class="row align-items-center">
                    <div class="col-md-3">
                        <div class="order-code">Đơn #${o.orderID}</div>
                        <div class="text-muted small">
                            <fmt:formatDate value="${o.orderDate}" pattern="dd/MM/yyyy HH:mm"/>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="text-muted small">Người nhận</div>
                        <div>${o.receiverName} - ${o.receiverPhone}</div>
                    </div>
                    <div class="col-md-3">
                        <span class="status-badge status-${o.status}">${o.status}</span>
                    </div>
                    <div class="col-md-2 order-price text-md-end">
                        <fmt:formatNumber value="${o.totalAmount}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                    </div>
                    <div class="col-md-1 text-md-end">
                        <a href="${pageContext.request.contextPath}/orders?action=detail&id=${o.orderID}"
                           class="btn btn-sm btn-outline-secondary">
                            <i class="bi bi-eye"></i>
                        </a>
                    </div>
                </div>
            </div>
        </c:forEach>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
