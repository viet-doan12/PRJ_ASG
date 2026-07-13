<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<style>
    :root {
        --flower-green: #2e7d32;
        --flower-green-dark: #1b5e20;
        --flower-soft: #e8f5e9;
        --flower-accent: #66bb6a;
    }
    .profile-hero {
        background: linear-gradient(135deg, var(--flower-soft) 0%, #fff 100%);
        border-radius: 1rem;
        border: 1px solid #dcedc8;
    }
    .avatar-circle {
        width: 72px;
        height: 72px;
        border-radius: 50%;
        background: var(--flower-green);
        color: #fff;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.75rem;
        font-weight: 600;
    }
    .card-section {
        border: none;
        border-radius: 1rem;
        box-shadow: 0 4px 18px rgba(46, 125, 50, 0.08);
    }
    .card-section .card-header {
        background: #fff;
        border-bottom: 1px solid #e8f5e9;
        font-weight: 600;
        color: var(--flower-green-dark);
        border-radius: 1rem 1rem 0 0 !important;
    }
    .btn-flower {
        background: var(--flower-green);
        border-color: var(--flower-green);
        color: #fff;
    }
    .btn-flower:hover {
        background: var(--flower-green-dark);
        border-color: var(--flower-green-dark);
        color: #fff;
    }
    .info-label {
        color: #6b7280;
        font-size: 0.85rem;
        margin-bottom: 0.15rem;
    }
    .info-value {
        font-weight: 500;
        color: #111827;
    }
    .role-badge {
        background: var(--flower-soft);
        color: var(--flower-green-dark);
        border: 1px solid #c8e6c9;
    }
</style>

<c:set var="u" value="${sessionScope.user}"/>
<c:set var="displayName" value="${not empty tmpFullName ? tmpFullName : u.fullName}"/>
<c:set var="displayPhone" value="${not empty tmpPhone ? tmpPhone : u.phone}"/>
<c:set var="displayAddress" value="${not empty tmpAddress ? tmpAddress : u.address}"/>

<main class="container py-4">
    <div class="profile-hero p-4 mb-4">
        <div class="d-flex flex-column flex-md-row align-items-md-center gap-3">
            <div class="avatar-circle">
                <c:choose>
                    <c:when test="${not empty u.fullName}">
                        ${u.fullName.substring(0,1).toUpperCase()}
                    </c:when>
                    <c:otherwise>?</c:otherwise>
                </c:choose>
            </div>
            <div class="flex-grow-1">
                <h1 class="h4 mb-1">${u.fullName}</h1>
                <div class="text-muted mb-2">
                    <i class="bi bi-envelope me-1"></i>${u.email}
                </div>
                <span class="badge role-badge rounded-pill px-3 py-2">
                    <i class="bi bi-shield-check me-1"></i>
                    Role ID: ${u.roleID}
                    <c:choose>
                        <c:when test="${u.roleID == 1}"> · Admin</c:when>
                        <c:when test="${u.roleID == 2}"> · Staff</c:when>
                        <c:otherwise> · Customer</c:otherwise>
                    </c:choose>
                </span>
            </div>
        </div>
    </div>

    <c:if test="${not empty message}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle me-1"></i>${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle me-1"></i>${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <div class="row g-4">
        <div class="col-lg-4">
            <div class="card card-section h-100">
                <div class="card-header py-3">
                    <i class="bi bi-info-circle me-1"></i>Thông tin tài khoản
                </div>
                <div class="card-body">
                    <div class="mb-3">
                        <div class="info-label">Họ và tên</div>
                        <div class="info-value">${u.fullName}</div>
                    </div>
                    <div class="mb-3">
                        <div class="info-label">Email</div>
                        <div class="info-value">${u.email}</div>
                    </div>
                    <div class="mb-3">
                        <div class="info-label">Số điện thoại</div>
                        <div class="info-value">
                            <c:out value="${empty u.phone ? '—' : u.phone}"/>
                        </div>
                    </div>
                    <div class="mb-3">
                        <div class="info-label">Địa chỉ</div>
                        <div class="info-value">
                            <c:out value="${empty u.address ? '—' : u.address}"/>
                        </div>
                    </div>
                    <div class="mb-0">
                        <div class="info-label">Trạng thái</div>
                        <div>
                            <c:choose>
                                <c:when test="${u.status}">
                                    <span class="badge text-bg-success">Đang hoạt động</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge text-bg-secondary">Đã khóa</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-lg-8">
            <div class="card card-section mb-4">
                <div class="card-header py-3">
                    <i class="bi bi-pencil-square me-1"></i>Cập nhật hồ sơ
                </div>
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/profile">
                        <input type="hidden" name="action" value="updateInfo">
                        <div class="row g-3">
                            <div class="col-md-6">
                                <label class="form-label" for="fullName">Họ và tên <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="fullName" name="fullName"
                                       value="<c:out value='${displayName}'/>" maxlength="100" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label" for="emailReadonly">Email</label>
                                <input type="email" class="form-control" id="emailReadonly"
                                       value="<c:out value='${u.email}'/>" readonly disabled>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label" for="phone">Số điện thoại</label>
                                <input type="text" class="form-control" id="phone" name="phone"
                                       value="<c:out value='${displayPhone}'/>"
                                       placeholder="0xxxxxxxxx" maxlength="20">
                                <div class="form-text">10 số, bắt đầu bằng 0 (để trống nếu không có).</div>
                            </div>
                            <div class="col-12">
                                <label class="form-label" for="address">Địa chỉ</label>
                                <textarea class="form-control" id="address" name="address" rows="3"
                                          maxlength="255"><c:out value="${displayAddress}"/></textarea>
                            </div>
                            <div class="col-12">
                                <button type="submit" class="btn btn-flower">
                                    <i class="bi bi-save me-1"></i>Lưu thay đổi
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <div class="card card-section">
                <div class="card-header py-3">
                    <i class="bi bi-key me-1"></i>Đổi mật khẩu
                </div>
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/profile" autocomplete="off">
                        <input type="hidden" name="action" value="changePassword">
                        <div class="row g-3">
                            <div class="col-md-12">
                                <label class="form-label" for="oldPassword">Mật khẩu hiện tại</label>
                                <input type="password" class="form-control" id="oldPassword"
                                       name="oldPassword" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label" for="newPassword">Mật khẩu mới</label>
                                <input type="password" class="form-control" id="newPassword"
                                       name="newPassword" required>
                                <div class="form-text">Tối thiểu 6 ký tự, có chữ và số.</div>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label" for="confirmPassword">Xác nhận mật khẩu mới</label>
                                <input type="password" class="form-control" id="confirmPassword"
                                       name="confirmPassword" required>
                            </div>
                            <div class="col-12">
                                <button type="submit" class="btn btn-outline-success">
                                    <i class="bi bi-shield-lock me-1"></i>Đổi mật khẩu
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />