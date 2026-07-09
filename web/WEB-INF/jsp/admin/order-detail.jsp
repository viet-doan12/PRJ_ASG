<%-- 
    Document   : order-detail
    Created on : Jul 4, 2026, 7:34:30 PM
    Author     : ADMIN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>

<style>
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

    .info-card {
        background: #fdf2f4;
        border-radius: 10px;
        padding: 20px;
    }
    .info-card h6 {
        color: #d63384;
        font-weight: 700;
        margin-bottom: 12px;
    }
    .detail-table {
        background: #fff;
        border-radius: 10px;
        overflow: hidden;
    }
    .detail-table th {
        background: #fdf2f4;
        font-weight: 600;
        border-bottom: none;
    }
    .item-img {
        width: 56px;
        height: 56px;
        object-fit: cover;
        border-radius: 8px;
        border: 1px solid #f0e0e4;
    }
    .grand-total {
        font-size: 18px;
        font-weight: 700;
        color: #d63384;
    }
    .update-status-card {
        background: #fff;
        border: 1px solid #f0e0e4;
        border-radius: 10px;
        padding: 18px 20px;
    }
    .btn-save-status {
        background: #d63384;
        border: none;
        color: #fff;
        font-weight: 600;
        border-radius: 20px;
        padding: 8px 24px;
    }
    .btn-save-status:hover {
        background: #b02a6f;
        color: #fff;
    }
</style>

<div class="row">
    <jsp:include page="/WEB-INF/jsp/common/sidebar.jsp"/>

    <div class="col-md-9 col-lg-10">
        <a href="${pageContext.request.contextPath}/admin/order" class="btn btn-sm btn-outline-secondary mb-3">
            <i class="bi bi-arrow-left"></i> Quay lại danh sách đơn hàng
        </a>

        <c:if test="${empty order}">
            <div class="alert alert-warning">Không tìm thấy đơn hàng.</div>
        </c:if>

        <c:if test="${not empty order}">
            <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
                <h4 class="fw-bold mb-0">Đơn hàng #${order.orderID}</h4>
                <span class="status-badge status-${order.status}">${order.status}</span>
            </div>

            <div class="row mb-3">
                <div class="col-md-6 mb-3">
                    <div class="info-card h-100">
                        <h6><i class="bi bi-truck me-1"></i>Thông tin giao hàng</h6>
                        <p class="mb-1"><strong>Người nhận:</strong> ${order.receiverName}</p>
                        <p class="mb-1"><strong>Điện thoại:</strong> ${order.receiverPhone}</p>
                        <p class="mb-0"><strong>Địa chỉ:</strong> ${order.shippingAddress}</p>
                    </div>
                </div>
                <div class="col-md-6 mb-3">
                    <div class="info-card h-100">
                        <h6><i class="bi bi-info-circle me-1"></i>Thông tin đơn hàng</h6>
                        <p class="mb-1"><strong>Ngày đặt:</strong>
                            <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/></p>
                        <p class="mb-1"><strong>Mã khách hàng:</strong> #${order.userID}</p>
                        <p class="mb-0"><strong>Tổng tiền:</strong>
                            <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></p>
                    </div>
                </div>
            </div>

            <!-- CẬP NHẬT TRẠNG THÁI (CHỈ ADMIN) -->
            <div class="update-status-card mb-4 d-flex flex-wrap gap-3 align-items-end justify-content-between">
                <form method="post" action="${pageContext.request.contextPath}/admin/order" class="row g-2 align-items-end m-0">
                    <input type="hidden" name="action" value="updateStatus">
                    <input type="hidden" name="orderID" value="${order.orderID}">
                    <div class="col-auto">
                        <label class="form-label mb-0 fw-semibold">Cập nhật trạng thái</label>
                        <select name="status" class="form-select">
                            <c:forEach var="s" items="${['Pending','Confirmed','Shipping','Completed','Cancelled']}">
                                <option value="${s}" ${order.status == s ? 'selected' : ''}>${s}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-auto">
                        <button type="submit" class="btn btn-save-status">
                            <i class="bi bi-check2-circle me-1"></i>Lưu thay đổi
                        </button>
                    </div>
                </form>

                <form method="post" action="${pageContext.request.contextPath}/admin/order" class="m-0"
                      onsubmit="return confirm('Bạn có chắc muốn xóa đơn hàng #${order.orderID}?');">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="orderID" value="${order.orderID}">
                    <button type="submit" class="btn btn-outline-danger">
                        <i class="bi bi-trash me-1"></i>Xóa đơn hàng
                    </button>
                </form>
            </div>

            <!-- CHI TIẾT SẢN PHẨM -->
            <div class="table-responsive detail-table shadow-sm">
                <table class="table align-middle mb-0">
                    <thead>
                        <tr>
                            <th>Sản phẩm</th>
                            <th class="text-center">Đơn giá</th>
                            <th class="text-center">Số lượng</th>
                            <th class="text-end">Thành tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty detailList}">
                                <tr>
                                    <td colspan="4" class="text-center text-muted py-4">Không có sản phẩm nào.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="d" items="${detailList}">
                                    <c:set var="flowerKey" value="flowerName_${d.flowerID}"/>
                                    <c:set var="imageKey" value="flowerImage_${d.flowerID}"/>
                                    <tr>
                                        <td>
                                            <div class="d-flex align-items-center gap-2">
                                                <c:choose>
                                                    <c:when test="${not empty requestScope[imageKey]}">
                                                        <img src="${pageContext.request.contextPath}/image/${requestScope[imageKey]}" class="item-img">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="${pageContext.request.contextPath}/image/no-image.png" class="item-img">
                                                    </c:otherwise>
                                                </c:choose>
                                                <span>${requestScope[flowerKey]}</span>
                                            </div>
                                        </td>
                                        <td class="text-center">
                                            <fmt:formatNumber value="${d.unitPrice}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                                        </td>
                                        <td class="text-center">${d.quantity}</td>
                                        <td class="text-end fw-semibold">
                                            <fmt:formatNumber value="${d.unitPrice * d.quantity}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                    <tfoot>
                        <tr>
                            <td colspan="3" class="text-end fw-bold">Tổng cộng</td>
                            <td class="text-end grand-total">
                                <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                            </td>
                        </tr>
                    </tfoot>
                </table>
            </div>
        </c:if>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
