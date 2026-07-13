<%-- 
    Document   : checkout
    Author     : ADMIN
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<style>
    .checkout-section-title {
        font-weight: 700;
        color: #198754;
        margin-bottom: 16px;
    }
    .checkout-card {
        background: #fff;
        border-radius: 12px;
        padding: 24px;
        box-shadow: 0 2px 10px rgba(0,0,0,.05);
    }
    .cart-item-row img {
        width: 56px;
        height: 56px;
        object-fit: cover;
        border-radius: 8px;
        border: 1px solid #eee;
    }
    .payment-option {
        border: 1px solid #dee2e6;
        border-radius: 10px;
        padding: 12px 16px;
        cursor: pointer;
        transition: all .2s ease;
    }
    .payment-option:hover {
        border-color: #198754;
        background: #f8fdfa;
    }
    .payment-option input:checked ~ .payment-label {
        color: #198754;
        font-weight: 700;
    }
    .grand-total-box {
        background: #f8fdfa;
        border-radius: 10px;
        padding: 16px 20px;
    }
    .address-mode-option {
        border: 1px solid #dee2e6;
        border-radius: 10px;
        padding: 12px 16px;
        cursor: pointer;
    }
    .address-mode-option input:checked ~ span {
        color: #198754;
        font-weight: 700;
    }
</style>

<div class="container my-5">

    <h3 class="fw-bold text-success mb-4">
        <i class="bi bi-bag-check-fill me-2"></i>Xác Nhận Đơn Hàng
    </h3>

    <c:if test="${not empty sessionScope.session_error}">
        <div class="alert alert-danger alert-dismissible fade show">
            <c:out value="${sessionScope.session_error}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="session_error" scope="session"/>
    </c:if>

    <c:if test="${not empty sessionScope.session_message}">
        <div class="alert alert-success alert-dismissible fade show">
            <c:out value="${sessionScope.session_message}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="session_message" scope="session"/>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/checkout" id="checkoutForm">

        <div class="row g-4">

            <!-- CỘT TRÁI: THÔNG TIN NGƯỜI NHẬN + ĐỊA CHỈ + THANH TOÁN -->
            <div class="col-lg-7">

                <div class="checkout-card mb-4">
                    <h5 class="checkout-section-title">
                        <i class="bi bi-person-fill me-2"></i>Thông Tin Người Nhận
                    </h5>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Họ và tên <span class="text-danger">*</span></label>
                        <input type="text" name="receiverName" class="form-control"
                               value="${fn:escapeXml(sessionScope.user.fullName)}"
                               placeholder="Nguyễn Văn A" required minlength="2" maxlength="100">
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Số điện thoại <span class="text-danger">*</span></label>
                        <input type="text" name="receiverPhone" class="form-control"
                               value="${fn:escapeXml(sessionScope.user.phone)}"
                               placeholder="09xxxxxxxx" pattern="0\d{9}" required
                               title="Số điện thoại gồm 10 số, bắt đầu bằng 0">
                    </div>

                    <div class="form-text">
                        <i class="bi bi-info-circle me-1"></i>Thông tin được điền sẵn từ hồ sơ của bạn, có thể chỉnh sửa nếu cần giao cho người khác.
                    </div>
                </div>

                <div class="checkout-card mb-4">
                    <h5 class="checkout-section-title">
                        <i class="bi bi-geo-alt-fill me-2"></i>Địa Chỉ Giao Hàng
                    </h5>

                    <c:choose>
                        <c:when test="${not empty sessionScope.user.address}">
                            <!-- Khách đã có địa chỉ lưu sẵn trong hồ sơ -->
                            <div class="mb-3">
                                <label class="address-mode-option d-flex align-items-center gap-2 mb-2">
                                    <input type="radio" name="addressMode" value="saved" id="modeSaved" checked>
                                    <span>Dùng địa chỉ đã lưu trong hồ sơ</span>
                                </label>
                                <div id="savedAddressBox" class="ps-4 mb-2 text-muted">
                                    <i class="bi bi-geo-alt me-1"></i><c:out value="${sessionScope.user.address}"/>
                                </div>

                                <label class="address-mode-option d-flex align-items-center gap-2">
                                    <input type="radio" name="addressMode" value="new" id="modeNew">
                                    <span>Nhập địa chỉ giao hàng khác</span>
                                </label>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <!-- Chưa có địa chỉ trong hồ sơ -> luôn bắt buộc nhập mới -->
                            <input type="hidden" id="modeSaved" value="">
                            <input type="hidden" id="modeNew" checked>
                        </c:otherwise>
                    </c:choose>

                    <div id="newAddressSection" class="${empty sessionScope.user.address ? '' : 'd-none'}">
                        <div class="row g-3 mb-3">
                            <div class="col-md-6">
                                <label class="form-label fw-semibold">Tỉnh/Thành phố <span class="text-danger">*</span></label>
                                <select id="province" class="form-select">
                                    <option value="">-- Đang tải... --</option>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-semibold">Phường/Xã <span class="text-danger">*</span></label>
                                <select id="ward" class="form-select" disabled>
                                    <option value="">-- Chọn Tỉnh/Thành trước --</option>
                                </select>
                            </div>
                        </div>

                        <div class="mb-2">
                            <label class="form-label fw-semibold">Địa chỉ cụ thể <span class="text-danger">*</span></label>
                            <input type="text" id="addressDetail" class="form-control"
                                   placeholder="Số nhà, tên đường...">
                        </div>
                    </div>

                    <!-- Trường thật sự được submit lên servlet -->
                    <input type="hidden" name="shippingAddress" id="shippingAddress">
                </div>

                <div class="checkout-card">
                    <h5 class="checkout-section-title">
                        <i class="bi bi-credit-card-fill me-2"></i>Phương Thức Thanh Toán
                    </h5>

                    <label class="payment-option d-flex align-items-center gap-2 mb-2">
                        <input type="radio" name="paymentMethod" value="COD" checked>
                        <span class="payment-label">
                            <i class="bi bi-cash-coin me-1"></i>Thanh toán khi nhận hàng (COD)
                        </span>
                    </label>

                    <label class="payment-option d-flex align-items-center gap-2">
                        <input type="radio" name="paymentMethod" value="Bank Transfer">
                        <span class="payment-label">
                            <i class="bi bi-bank me-1"></i>Chuyển khoản ngân hàng
                        </span>
                    </label>
                </div>

            </div>

            <!-- CỘT PHẢI: TÓM TẮT ĐƠN HÀNG -->
            <div class="col-lg-5">
                <div class="checkout-card">
                    <h5 class="checkout-section-title">
                        <i class="bi bi-basket-fill me-2"></i>Đơn Hàng Của Bạn
                    </h5>

                    <div class="mb-3" style="max-height: 340px; overflow-y: auto;">
                        <c:forEach var="item" items="${cartItems}">
                            <div class="d-flex align-items-center gap-3 cart-item-row mb-3 pb-3 border-bottom">
                                <img src="${pageContext.request.contextPath}/images/flowers/${item.flower.image}" alt="${item.flower.flowerName}">
                                <div class="flex-grow-1">
                                    <div class="fw-semibold">${item.flower.flowerName}</div>
                                    <div class="text-muted small">
                                        <fmt:formatNumber value="${item.flower.price}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                                        &times; ${item.quantity}
                                    </div>
                                </div>
                                <div class="fw-semibold text-success">
                                    <fmt:formatNumber value="${item.flower.price * item.quantity}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <div class="grand-total-box d-flex justify-content-between align-items-center mb-3">
                        <span class="fw-bold fs-5">Tổng cộng</span>
                        <span class="fw-bold fs-4 text-success">
                            <fmt:formatNumber value="${cartTotal}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                        </span>
                    </div>

                    <button type="submit" class="btn btn-success btn-lg w-100 fw-bold rounded-pill" id="submitOrderBtn">
                        <i class="bi bi-check-circle-fill me-2"></i>Đặt Hàng Ngay
                    </button>

                    <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-secondary w-100 rounded-pill mt-2">
                        <i class="bi bi-arrow-left me-1"></i>Quay lại giỏ hàng
                    </a>
                </div>
            </div>

        </div>
    </form>
</div>

<script>
(function () {
    const API_BASE = "https://provinces.open-api.vn/api/v2";

    const provinceSelect = document.getElementById("province");
    const wardSelect = document.getElementById("ward");
    const addressDetailInput = document.getElementById("addressDetail");
    const shippingAddressHidden = document.getElementById("shippingAddress");
    const form = document.getElementById("checkoutForm");
    const newAddressSection = document.getElementById("newAddressSection");
    const modeSaved = document.getElementById("modeSaved");
    const modeNew = document.getElementById("modeNew");
    const savedAddressText = <c:choose>
        <c:when test="${not empty sessionScope.user.address}">"${fn:escapeXml(sessionScope.user.address)}"</c:when>
        <c:otherwise>""</c:otherwise>
    </c:choose>;

    let selectedProvinceName = "";
    let selectedWardName = "";
    let provincesLoaded = false;

    function resetSelect(selectEl, placeholder) {
        selectEl.innerHTML = `<option value="">${placeholder}</option>`;
        selectEl.disabled = true;
    }

    function fillSelect(selectEl, items, placeholder) {
        selectEl.innerHTML = `<option value="">${placeholder}</option>`;
        items.forEach(function (item) {
            const opt = document.createElement("option");
            opt.value = item.code;
            opt.textContent = item.name;
            selectEl.appendChild(opt);
        });
        selectEl.disabled = false;
    }

    function loadProvincesIfNeeded() {
        if (provincesLoaded) return;
        provincesLoaded = true;
        fetch(API_BASE + "/p/")
            .then(function (res) { return res.json(); })
            .then(function (provinces) {
                fillSelect(provinceSelect, provinces, "-- Chọn Tỉnh/Thành phố --");
            })
            .catch(function () {
                provinceSelect.innerHTML = '<option value="">Không tải được danh sách, vui lòng thử lại</option>';
            });
    }

    // Chỉ tải danh sách tỉnh/thành khi thực sự cần (khách chọn "nhập địa chỉ khác",
    // hoặc khách chưa có địa chỉ trong hồ sơ nên khu vực này hiện sẵn ngay từ đầu)
    if (!modeSaved || newAddressSection.classList.contains("d-none") === false) {
        loadProvincesIfNeeded();
    }

    // Toggle giữa "dùng địa chỉ đã lưu" và "nhập địa chỉ mới"
    if (modeSaved && modeNew && modeSaved.tagName === "INPUT" && modeSaved.type === "radio") {
        modeSaved.addEventListener("change", function () {
            newAddressSection.classList.add("d-none");
        });
        modeNew.addEventListener("change", function () {
            newAddressSection.classList.remove("d-none");
            loadProvincesIfNeeded();
        });
    }

    provinceSelect.addEventListener("change", function () {
        selectedProvinceName = this.options[this.selectedIndex]?.text || "";
        resetSelect(wardSelect, "-- Đang tải... --");

        const provinceCode = this.value;
        if (!provinceCode) {
            resetSelect(wardSelect, "-- Chọn Tỉnh/Thành trước --");
            return;
        }

        fetch(API_BASE + "/p/" + provinceCode + "?depth=2")
            .then(function (res) { return res.json(); })
            .then(function (data) {
                fillSelect(wardSelect, data.wards || [], "-- Chọn Phường/Xã --");
            })
            .catch(function () {
                wardSelect.innerHTML = '<option value="">Lỗi tải dữ liệu</option>';
            });
    });

    wardSelect.addEventListener("change", function () {
        selectedWardName = this.options[this.selectedIndex]?.text || "";
    });

    form.addEventListener("submit", function (e) {
        const usingSavedAddress = modeSaved && modeSaved.type === "radio" && modeSaved.checked;

        if (usingSavedAddress) {
            // Dùng thẳng địa chỉ đã lưu trong hồ sơ, không cần qua dropdown
            shippingAddressHidden.value = savedAddressText;
            return; // cho submit tiếp tục bình thường
        }

        // Chế độ nhập địa chỉ mới -> bắt buộc chọn Tỉnh/Thành + Phường/Xã + địa chỉ cụ thể
        if (!provinceSelect.value || !wardSelect.value) {
            e.preventDefault();
            alert("Vui lòng chọn đầy đủ Tỉnh/Thành phố và Phường/Xã.");
            return;
        }
        if (!addressDetailInput.value.trim()) {
            e.preventDefault();
            alert("Vui lòng nhập địa chỉ cụ thể (số nhà, tên đường...).");
            return;
        }

        const fullAddress = [
            addressDetailInput.value.trim(),
            selectedWardName,
            selectedProvinceName
        ].filter(Boolean).join(", ");

        shippingAddressHidden.value = fullAddress;
    });
})();
</script>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" /></parameter>