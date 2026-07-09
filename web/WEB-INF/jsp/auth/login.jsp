<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
<div class="row justify-content-center align-items-center my-5"
     style="min-height: 85vh;
            background-image: url('${pageContext.request.contextPath}/image/login-bg.jpg');
            background-size: cover;
            background-position: center;
            background-repeat: no-repeat;">
    <div class="col-lg-5 col-md-7 col-sm-10">
        <div class="card shadow border-0" style="background-color: rgba(255, 255, 255, 0.92);">
            <div class="card-body p-4">
                <h2 class="text-center text-success mb-4">
                    <i class="bi bi-box-arrow-in-right"></i>
                    Đăng nhập
                </h2>
                <form method="post"
                      action="${pageContext.request.contextPath}/login">
                    <div class="mb-3">
                        <label class="form-label">Email</label>
                        <input type="email" class="form-control" name="email"
                               value="${email}" placeholder="Nhập email" required autofocus>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Mật khẩu</label>
                        <input type="password" class="form-control" name="password"
                               placeholder="Nhập mật khẩu" required>
                    </div>
                    <button class="btn btn-success w-100" type="submit">Đăng nhập</button>
                </form>
                <hr>
                <div class="d-flex justify-content-between">
                    <a href="${pageContext.request.contextPath}/forgot-password">Quên mật khẩu?</a>
                    <a href="${pageContext.request.contextPath}/register">Đăng ký</a>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>