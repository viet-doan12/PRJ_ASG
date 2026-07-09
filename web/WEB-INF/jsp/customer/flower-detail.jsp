<%-- 
    Document   : flower-detail
    Created on : Jul 4, 2026, 7:35:04 PM
    Author     : ADMIN
--%>

<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<style>
    /* ===== FlowerCorner-inspired theme (chỉ áp dụng trong trang checkout) ===== */
    .fc-page .card { border-radius: 18px; }
    .fc-btn-primary {
        background: linear-gradient(135deg, #ff4fa3, #e4007c);
        border: none; color: #fff; font-weight: 700; letter-spacing: .4px;
        border-radius: 50px; padding: .7rem 1.6rem; text-transform: uppercase;
        transition: all .2s ease; font-size: .92rem;
    }
    .fc-btn-primary:hover, .fc-btn-primary:focus {
        background: linear-gradient(135deg, #e4007c, #a8005f); color: #fff;
        box-shadow: 0 8px 18px rgba(224,22,108,.35);
    }
    .fc-btn-outline {
        border: 1.5px solid #e4007c; color: #e4007c; background: #fff;
        border-radius: 50px; font-weight: 600; padding: .6rem 1.4rem; font-size: .9rem;
    }
    .fc-btn-outline:hover { background: #fff0f6; color: #a8005f; border-color: #a8005f; }
    .fc-price { color: #e4007c; font-weight: 700; }
    .fc-thumb { border-radius: 12px; object-fit: cover; border: 1px solid #f7c9e0; }
    .fc-badge-trust {
        background: #fff0f6; color: #a8005f; border-radius: 50px;
        padding: .45rem 1rem; font-size: .82rem; font-weight: 600;
        display: inline-flex; align-items: center; gap: .4rem; white-space: nowrap;
    }
    .fc-summary-card, .fc-form-card { border: 1px solid #f7c9e0; }
    .fc-total-box { background: #fff0f6; border-radius: 14px; padding: 1rem; }
    .fc-section-title { color: #a8005f; }
    .fc-form-control:focus { border-color: #e4007c; box-shadow: 0 0 0 .2rem rgba(224,22,108,.15); }
    .fc-pay-option {
        border: 1.5px solid #f2a9cf; border-radius: 14px; transition: all .15s ease; cursor: pointer;
    }
    .fc-pay-option.selected { border-color: #e4007c; background: #fff0f6; }
</style>

<div class="checkout-page fc-page mb-5">
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
        <h2 class="fw-bold mb-0" style="color:#a8005f;">
            <i class="bi bi-credit-card me-2"></i>Xác nhận đơn hàng
        </h2>
        <div class="d-flex flex-wrap gap-2">
            <span class="fc-badge-trust"><i class="bi bi-lightning-charge-fill"></i> Giao nhanh 60 phút</span>
            <span class="fc-badge-trust"><i class="bi bi-flower2"></i> Hoa tươi mỗi ngày</span>
        </div>
    </div>

    <form action="${pageContext.request.contextPath}/checkout" method="post" id="checkoutForm">
        <div class="row g-4">
            <!-- Thông tin nhận hàng + phương thức thanh toán -->
            <div class="col-lg-7">
                <div class="card border-0 shadow-sm fc-form-card mb-4">
                    <div class="card-body">
                        <h5 class="fw-bold mb-3 fc-section-title"><i class="bi bi-truck me-2"></i>Thông tin giao hàng</h5>

                        <div class="mb-3">
                            <label class="form-label">Họ tên người nhận <span class="text-danger">*</span></label>
                            <input type="text" name="receiverName" class="form-control fc-form-control"
                                   value="${sessionScope.user.fullName}"
                                   minlength="2" maxlength="100" required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Số điện thoại <span class="text-danger">*</span></label>
                            <input type="tel" name="receiverPhone" class="form-control fc-form-control"
                                   value="${sessionScope.user.phone}"
                                   pattern="0\d{9}" title="Số điện thoại gồm 10 số, bắt đầu bằng 0"
                                   placeholder="0xxxxxxxxx" required>
                        </div>

                        <div class="mb-1">
                            <label class="form-label">Địa chỉ giao hàng <span class="text-danger">*</span></label>
                            <textarea name="shippingAddress" class="form-control fc-form-control" rows="3"
                                      placeholder="Số nhà, đường, phường/xã, quận/huyện, tỉnh/thành phố" required>${sessionScope.user.address}</textarea>
                        </div>
                    </div>
                </div>

                <div class="card border-0 shadow-sm fc-form-card">
                    <div class="card-body">
                        <h5 class="fw-bold mb-3 fc-section-title"><i class="bi bi-wallet2 me-2"></i>Phương thức thanh toán</h5>

                        <label class="fc-pay-option d-block p-3 mb-2 selected">
                            <input class="form-check-input me-2" type="radio" name="paymentMethod" value="COD"
                                   checked onchange="selectPayOption(this)">
                            <i class="bi bi-cash-coin me-1" style="color:#e4007c;"></i> Thanh toán khi nhận hàng (COD)
                        </label>

                        <label class="fc-pay-option d-block p-3">
                            <input class="form-check-input me-2" type="radio" name="paymentMethod" value="Bank Transfer"
                                   onchange="selectPayOption(this)">
                            <i class="bi bi-bank me-1" style="color:#e4007c;"></i> Chuyển khoản ngân hàng
                        </label>
                    </div>
                </div>
            </div>

            <!-- Tóm tắt đơn hàng -->
            <div class="col-lg-5">
                <div class="card border-0 shadow-sm fc-summary-card">
                    <div class="card-body">
                        <h5 class="fw-bold mb-3 fc-section-title">Đơn hàng của bạn</h5>

                        <div class="mb-3" style="max-height: 320px; overflow-y: auto;">
                            <c:forEach var="item" items="${requestScope.cartItems}">
                                <c:set var="flower" value="${item.flower}" />
                                <div class="d-flex align-items-center mb-2 pb-2 border-bottom">
                                    <img src="${pageContext.request.contextPath}/images/${flower.image}"
                                         onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/image/05b4fbc3f169175e6deb97b3977175b6.jpg';"
                                         alt="${flower.flowerName}"
                                         class="fc-thumb me-2"
                                         style="width:48px;height:48px;">
                                    <div class="flex-grow-1">
                                        <div class="fw-semibold small">${flower.flowerName}</div>
                                        <div class="text-muted small">
                                            <fmt:formatNumber value="${flower.price}" type="number" groupingUsed="true"/>&nbsp;đ
                                            &times; ${item.quantity}
                                        </div>
                                    </div>
                                    <div class="fw-semibold small fc-price">
                                        <fmt:formatNumber value="${flower.price * item.quantity}" type="number" groupingUsed="true"/>&nbsp;đ
                                    </div>
                                </div>
                            </c:forEach>
                        </div>

                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Tạm tính</span>
                            <span><fmt:formatNumber value="${requestScope.cartTotal}" type="number" groupingUsed="true"/>&nbsp;đ</span>
                        </div>
                        <div class="d-flex justify-content-between mb-3 text-muted small">
                            <span>Phí vận chuyển</span>
                            <span>Miễn phí</span>
                        </div>

                        <div class="fc-total-box d-flex justify-content-between align-items-center mb-3">
                            <span class="fw-bold">Tổng cộng</span>
                            <span class="fw-bold fs-5 fc-price">
                                <fmt:formatNumber value="${requestScope.cartTotal}" type="number" groupingUsed="true"/>&nbsp;đ
                            </span>
                        </div>

                        <button type="submit" class="fc-btn-primary w-100">
                            <i class="bi bi-check-circle me-1"></i> Đặt hàng
                        </button>
                        <a href="${pageContext.request.contextPath}/cart" class="fc-btn-outline d-block text-center text-decoration-none mt-2">
                            <i class="bi bi-arrow-left me-1"></i> Quay lại giỏ hàng
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>

<script>
    function selectPayOption(radio) {
        var options = document.getElementsByClassName('fc-pay-option');
        for (var i = 0; i < options.length; i++) {
            options[i].classList.remove('selected');
        }
        radio.closest('.fc-pay-option').classList.add('selected');
    }
</script>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

