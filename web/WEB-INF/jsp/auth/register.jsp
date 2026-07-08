<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%> %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
<div class="row justify-content-center my-5">
    <div class="col-lg-6 col-md-8 col-sm-10">
        <div class="card shadow border-0">
            <div class="card-body p-4">
                <h2 class="text-center text-success mb-4">
                    <i class="bi bi-person-plus"></i>
                    Đăng ký
                </h2>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert">
                        ${error}
                    </div>
                </c:if>

                <form method="post"
                      action="${pageContext.request.contextPath}/register">
                    <div class="mb-3">
                        <label class="form-label">Họ và tên</label>
                        <input
                            type="text"
                            class="form-control"
                            name="fullName"
                            value="${fullName}"
                            placeholder="Nhập họ và tên"
                            required
                            autofocus>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Email</label>
                        <input
                            type="email"
                            class="form-control"
                            name="email"
                            value="${email}"
                            placeholder="Nhập email"
                            required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Số điện thoại</label>
                        <input
                            type="text"
                            class="form-control"
                            name="phone"
                            value="${phone}"
                            placeholder="Nhập số điện thoại"
                            pattern="[0-9]{9,11}"
                            required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Địa chỉ</label>
                        <input
                            type="text"
                            class="form-control"
                            name="address"
                            value="${address}"
                            placeholder="Nhập địa chỉ"
                            required>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Mật khẩu</label>
                            <input
                                type="password"
                                class="form-control"
                                name="password"
                                placeholder="Nhập mật khẩu"
                                minlength="6"
                                required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Xác nhận mật khẩu</label>
                            <input
                                type="password"
                                class="form-control"
                                name="confirmPassword"
                                placeholder="Nhập lại mật khẩu"
                                minlength="6"
                                required>
                        </div>
                    </div>
                    <button class="btn btn-success w-100" type="submit">
                        Đăng ký
                    </button>
                </form>
                <hr>
                <div class="text-center">
                    Đã có tài khoản?
                    <a href="${pageContext.request.contextPath}/login">
                        Đăng nhập
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>