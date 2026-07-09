<%-- 
    Document   : orders
    Created on : Jul 4, 2026, 7:34:21 PM
    Author     : ADMIN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>

<div class="row">
    <jsp:include page="/WEB-INF/jsp/common/sidebar.jsp"/>

    <div class="col-md-9 col-lg-10">
        <h3 class="fw-bold text-success mb-4"><i class="bi bi-receipt-cutoff me-2"></i>Quản lý đơn hàng</h3>

        <!-- FORM TÌM KIẾM + LỌC TRẠNG THÁI -->
        <form method="get" action="${pageContext.request.contextPath}/admin/order" class="row g-2 mb-3">
            <div class="col-md-5">
                <input type="text" name="keyword" class="form-control"
                       placeholder="Tìm theo tên/SĐT người nhận hoặc mã đơn..."
                       value="${keyword}">
            </div>
            <div class="col-md-3">
                <select name="status" class="form-select">
                    <option value="">-- Tất cả trạng thái --</option>
                    <c:forEach var="s" items="${['Pending','Confirmed','Shipping','Completed','Cancelled']}">
                        <option value="${s}" ${status == s ? 'selected' : ''}>${s}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-2">
                <button type="submit" class="btn btn-success w-100">
                    <i class="bi bi-search me-1"></i>Tìm
                </button>
            </div>
            <div class="col-md-2">
                <a href="${pageContext.request.contextPath}/admin/order" class="btn btn-outline-secondary w-100">
                    Xóa lọc
                </a>
            </div>
        </form>

        <!-- BẢNG DANH SÁCH ĐƠN HÀNG -->
        <div class="table-responsive shadow-sm">
            <table class="table table-hover align-middle bg-white">
                <thead class="table-success">
                    <tr>
                        <th>#</th>
                        <th>Người nhận</th>
                        <th>SĐT</th>
                        <th>Địa chỉ giao</th>
                        <th>Ngày đặt</th>
                        <th>Tổng tiền</th>
                        <th>Trạng thái</th>
                        <th class="text-center">Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty orderList}">
                            <tr>
                                <td colspan="8" class="text-center text-muted py-4">Không có đơn hàng nào.</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="o" items="${orderList}">
                                <tr>
                                    <td>#${o.orderID}</td>
                                    <td>${o.receiverName}</td>
                                    <td>${o.receiverPhone}</td>
                                    <td>${o.shippingAddress}</td>
                                    <td><fmt:formatDate value="${o.orderDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                                    <td><fmt:formatNumber value="${o.totalAmount}" type="currency" currencySymbol="đ"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${o.status == 'Completed'}">
                                                <span class="badge bg-success">${o.status}</span>
                                            </c:when>
                                            <c:when test="${o.status == 'Cancelled'}">
                                                <span class="badge bg-danger">${o.status}</span>
                                            </c:when>
                                            <c:when test="${o.status == 'Pending'}">
                                                <span class="badge bg-warning text-dark">${o.status}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-info text-dark">${o.status}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <a href="${pageContext.request.contextPath}/admin/order?action=detail&id=${o.orderID}"
                                           class="btn btn-sm btn-outline-primary">
                                            <i class="bi bi-eye"></i> Chi tiết
                                        </a>
                                        <form method="post" action="${pageContext.request.contextPath}/admin/order"
                                              class="d-inline"
                                              onsubmit="return confirm('Bạn có chắc muốn xóa đơn hàng #${o.orderID}?');">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="orderID" value="${o.orderID}">
                                            <button type="submit" class="btn btn-sm btn-outline-danger">
                                                <i class="bi bi-trash"></i>
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

        <!-- PHÂN TRANG -->
        <jsp:include page="/WEB-INF/jsp/common/pagination.jsp"/>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
