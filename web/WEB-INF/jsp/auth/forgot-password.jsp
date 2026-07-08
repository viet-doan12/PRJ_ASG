<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>

<div class="row justify-content-center mb-5">

    <div class="col-lg-5 col-md-7 col-sm-10">

        <div class="card shadow-sm border-0">

            <div class="card-body p-4">

                <h2 class="text-center text-success fw-bold mb-3">
                    <i class="bi bi-shield-lock me-2"></i>
                    Quên mật khẩu
                </h2>

                <p class="text-center text-muted mb-4">
                    Nhập email đã đăng ký. Chúng tôi sẽ gửi mã OTP để xác minh và cho phép bạn đặt lại mật khẩu.
                </p>

                <form method="post"
                      action="${pageContext.request.contextPath}/forgot-password">

                    <div class="mb-4">

                        <label for="email" class="form-label fw-medium">
                            Địa chỉ Email
                        </label>

                        <div class="input-group">

                            <span class="input-group-text bg-light border-end-0">
                                <i class="bi bi-envelope"></i>
                            </span>

                            <input
                                type="email"
                                id="email"
                                name="email"
                                class="form-control border-start-0 ps-0"
                                placeholder="example@gmail.com"
                                value="${email}"
                                required
                                autofocus>

                        </div>

                    </div>

                    <button type="submit"
                            class="btn btn-success w-100 py-2 fw-bold">

                        <i class="bi bi-send-fill me-2"></i>
                        Gửi mã OTP

                    </button>

                </form>

                <hr class="my-4">

                <div class="d-flex justify-content-between flex-wrap">

                    <a href="${pageContext.request.contextPath}/login"
                       class="text-decoration-none">

                        <i class="bi bi-arrow-left me-1"></i>
                        Quay lại đăng nhập

                    </a>

                    <a href="${pageContext.request.contextPath}/register"
                       class="text-decoration-none text-success">

                        Chưa có tài khoản?

                    </a>

                </div>

            </div>

        </div>

    </div>

</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>